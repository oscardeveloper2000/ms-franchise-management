package com.epam.franquicias.infrastructure.adapter.in.web;

import com.epam.franquicias.application.exception.FranquiciaNoEncontradaException;
import com.epam.franquicias.application.exception.ProductoNoEncontradoException;
import com.epam.franquicias.application.exception.SucursalNoEncontradaException;
import com.epam.franquicias.application.port.in.ActualizarNombreFranquiciaUseCase;
import com.epam.franquicias.application.port.in.ActualizarNombreProductoUseCase;
import com.epam.franquicias.application.port.in.ActualizarNombreSucursalUseCase;
import com.epam.franquicias.application.port.in.AgregarFranquiciaUseCase;
import com.epam.franquicias.application.port.in.AgregarProductoUseCase;
import com.epam.franquicias.application.port.in.AgregarSucursalUseCase;
import com.epam.franquicias.application.port.in.EliminarProductoUseCase;
import com.epam.franquicias.application.port.in.ModificarStockUseCase;
import com.epam.franquicias.application.port.in.ObtenerProductoMasStockPorSucursalUseCase;
import com.epam.franquicias.application.port.in.ProductoPorSucursal;
import com.epam.franquicias.domain.model.Franquicia;
import com.epam.franquicias.infrastructure.adapter.in.web.dto.ActualizarNombreRequest;
import com.epam.franquicias.infrastructure.adapter.in.web.dto.ActualizarStockRequest;
import com.epam.franquicias.infrastructure.adapter.in.web.dto.CrearFranquiciaRequest;
import com.epam.franquicias.infrastructure.adapter.in.web.dto.CrearProductoRequest;
import com.epam.franquicias.infrastructure.adapter.in.web.dto.CrearSucursalRequest;
import com.epam.franquicias.infrastructure.adapter.in.web.dto.FranquiciaResponse;
import com.epam.franquicias.infrastructure.adapter.in.web.dto.ProductoPorSucursalResponse;
import com.epam.franquicias.infrastructure.adapter.in.web.dto.ProductoResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import java.util.Set;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class FranquiciaHandler {

    private final AgregarFranquiciaUseCase agregarFranquiciaUseCase;
    private final AgregarSucursalUseCase agregarSucursalUseCase;
    private final AgregarProductoUseCase agregarProductoUseCase;
    private final EliminarProductoUseCase eliminarProductoUseCase;
    private final ModificarStockUseCase modificarStockUseCase;
    private final ObtenerProductoMasStockPorSucursalUseCase obtenerProductoMasStockPorSucursalUseCase;
    private final ActualizarNombreFranquiciaUseCase actualizarNombreFranquiciaUseCase;
    private final ActualizarNombreSucursalUseCase actualizarNombreSucursalUseCase;
    private final ActualizarNombreProductoUseCase actualizarNombreProductoUseCase;
    private final Validator validator;

    public FranquiciaHandler(
            AgregarFranquiciaUseCase agregarFranquiciaUseCase,
            AgregarSucursalUseCase agregarSucursalUseCase,
            AgregarProductoUseCase agregarProductoUseCase,
            EliminarProductoUseCase eliminarProductoUseCase,
            ModificarStockUseCase modificarStockUseCase,
            ObtenerProductoMasStockPorSucursalUseCase obtenerProductoMasStockPorSucursalUseCase,
            ActualizarNombreFranquiciaUseCase actualizarNombreFranquiciaUseCase,
            ActualizarNombreSucursalUseCase actualizarNombreSucursalUseCase,
            ActualizarNombreProductoUseCase actualizarNombreProductoUseCase,
            Validator validator) {
        this.agregarFranquiciaUseCase = agregarFranquiciaUseCase;
        this.agregarSucursalUseCase = agregarSucursalUseCase;
        this.agregarProductoUseCase = agregarProductoUseCase;
        this.eliminarProductoUseCase = eliminarProductoUseCase;
        this.modificarStockUseCase = modificarStockUseCase;
        this.obtenerProductoMasStockPorSucursalUseCase = obtenerProductoMasStockPorSucursalUseCase;
        this.actualizarNombreFranquiciaUseCase = actualizarNombreFranquiciaUseCase;
        this.actualizarNombreSucursalUseCase = actualizarNombreSucursalUseCase;
        this.actualizarNombreProductoUseCase = actualizarNombreProductoUseCase;
        this.validator = validator;
    }

    public Mono<ServerResponse> crearFranquicia(ServerRequest request) {
        return validarYLeerBody(request, CrearFranquiciaRequest.class)
                .flatMap(dto -> agregarFranquiciaUseCase.ejecutar(dto.nombre())
                        .map(FranquiciaResponse::toResponse)
                        .flatMap(response -> ServerResponse.status(HttpStatus.CREATED)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(response)));
    }

    public Mono<ServerResponse> agregarSucursal(ServerRequest request) {
        UUID franquiciaId = UUID.fromString(request.pathVariable("franquiciaId"));
        return validarYLeerBody(request, CrearSucursalRequest.class)
                .flatMap(dto -> agregarSucursalUseCase.ejecutar(franquiciaId, dto.nombre())
                        .map(FranquiciaResponse::toResponse)
                        .flatMap(response -> ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(response)));
    }

    public Mono<ServerResponse> agregarProducto(ServerRequest request) {
        UUID franquiciaId = UUID.fromString(request.pathVariable("franquiciaId"));
        UUID sucursalId = UUID.fromString(request.pathVariable("sucursalId"));
        return validarYLeerBody(request, CrearProductoRequest.class)
                .flatMap(dto -> agregarProductoUseCase
                        .ejecutar(franquiciaId, sucursalId, dto.nombre(), dto.cantidadStock())
                        .map(FranquiciaResponse::toResponse)
                        .flatMap(response -> ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(response)));
    }

    public Mono<ServerResponse> eliminarProducto(ServerRequest request) {
        UUID franquiciaId = UUID.fromString(request.pathVariable("franquiciaId"));
        UUID sucursalId = UUID.fromString(request.pathVariable("sucursalId"));
        UUID productoId = UUID.fromString(request.pathVariable("productoId"));
        return eliminarProductoUseCase.ejecutar(franquiciaId, sucursalId, productoId)
                .map(FranquiciaResponse::toResponse)
                .flatMap(response -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(response));
    }

    public Mono<ServerResponse> modificarStock(ServerRequest request) {
        UUID franquiciaId = UUID.fromString(request.pathVariable("franquiciaId"));
        UUID sucursalId = UUID.fromString(request.pathVariable("sucursalId"));
        UUID productoId = UUID.fromString(request.pathVariable("productoId"));
        return validarYLeerBody(request, ActualizarStockRequest.class)
                .flatMap(
                        dto -> modificarStockUseCase.ejecutar(franquiciaId, sucursalId, productoId, dto.cantidadStock())
                                .map(FranquiciaResponse::toResponse)
                                .flatMap(response -> ServerResponse.ok()
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .bodyValue(response)));
    }

    public Mono<ServerResponse> obtenerTopStock(ServerRequest request) {
        UUID franquiciaId = UUID.fromString(request.pathVariable("franquiciaId"));
        Flux<ProductoPorSucursalResponse> response = obtenerProductoMasStockPorSucursalUseCase.ejecutar(franquiciaId)
                .map(this::toResponse);

        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(response, ProductoPorSucursalResponse.class);
    }

    public Mono<ServerResponse> actualizarNombreFranquicia(ServerRequest request) {
        UUID franquiciaId = UUID.fromString(request.pathVariable("franquiciaId"));
        return validarYLeerBody(request, ActualizarNombreRequest.class)
                .flatMap(dto -> actualizarNombreFranquiciaUseCase.ejecutar(franquiciaId, dto.nombre())
                        .map(FranquiciaResponse::toResponse)
                        .flatMap(response -> ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(response)));
    }

    public Mono<ServerResponse> actualizarNombreSucursal(ServerRequest request) {
        UUID franquiciaId = UUID.fromString(request.pathVariable("franquiciaId"));
        UUID sucursalId = UUID.fromString(request.pathVariable("sucursalId"));
        return validarYLeerBody(request, ActualizarNombreRequest.class)
                .flatMap(dto -> actualizarNombreSucursalUseCase.ejecutar(franquiciaId, sucursalId, dto.nombre())
                        .map(FranquiciaResponse::toResponse)
                        .flatMap(response -> ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(response)));
    }

    public Mono<ServerResponse> actualizarNombreProducto(ServerRequest request) {
        UUID franquiciaId = UUID.fromString(request.pathVariable("franquiciaId"));
        UUID sucursalId = UUID.fromString(request.pathVariable("sucursalId"));
        UUID productoId = UUID.fromString(request.pathVariable("productoId"));
        return validarYLeerBody(request, ActualizarNombreRequest.class)
                .flatMap(dto -> actualizarNombreProductoUseCase
                        .ejecutar(franquiciaId, sucursalId, productoId, dto.nombre())
                        .map(FranquiciaResponse::toResponse)
                        .flatMap(response -> ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(response)));
    }

    private <T> Mono<T> validarYLeerBody(ServerRequest request, Class<T> type) {
        return request.bodyToMono(type)
                .flatMap(dto -> {
                    Set<ConstraintViolation<T>> violations = validator.validate(dto);
                    if (!violations.isEmpty()) {
                        throw new ConstraintViolationException(violations);
                    }
                    return Mono.just(dto);
                });
    }

    private ProductoPorSucursalResponse toResponse(ProductoPorSucursal item) {
        return new ProductoPorSucursalResponse(
                item.sucursalId(),
                item.nombreSucursal(),
                ProductoResponse.toResponse(item.producto()));
    }
}
