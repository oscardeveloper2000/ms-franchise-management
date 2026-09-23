package com.epam.franquicias.infrastructure.adapter.in.web;

import com.epam.franquicias.application.exception.FranquiciaNoEncontradaException;
import com.epam.franquicias.application.exception.ProductoNoEncontradoException;
import com.epam.franquicias.application.exception.SucursalNoEncontradaException;
import com.epam.franquicias.domain.exception.DomainValidationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Order(-2)
public class GlobalErrorWebExceptionHandler implements ErrorWebExceptionHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        HttpStatus status;
        String mensaje;

        if (ex instanceof DomainValidationException) {
            status = HttpStatus.BAD_REQUEST;
            mensaje = ex.getMessage();
        } else if (ex instanceof ConstraintViolationException validationException) {
            status = HttpStatus.BAD_REQUEST;
            mensaje = validationException.getConstraintViolations().stream()
                    .map(ConstraintViolation::getMessage)
                    .reduce((left, right) -> left + "; " + right)
                    .orElse("Los datos enviados no son válidos");
        } else if (ex instanceof FranquiciaNoEncontradaException
                || ex instanceof SucursalNoEncontradaException
                || ex instanceof ProductoNoEncontradoException) {
            status = HttpStatus.NOT_FOUND;
            mensaje = ex.getMessage();
        } else {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            mensaje = "Ocurrió un error inesperado";
        }

        ErrorResponse body = new ErrorResponse(mensaje, status.value());
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        try {
            byte[] bytes = objectMapper.writeValueAsBytes(body);
            return response.writeWith(Mono.just(response.bufferFactory().wrap(bytes)));
        } catch (JsonProcessingException e) {
            return response.writeWith(Mono.just(response.bufferFactory().wrap(
                    "{\"mensaje\":\"Ocurrió un error inesperado\",\"status\":500}".getBytes())));
        }
    }

    public record ErrorResponse(String mensaje, int status) {
    }
}
