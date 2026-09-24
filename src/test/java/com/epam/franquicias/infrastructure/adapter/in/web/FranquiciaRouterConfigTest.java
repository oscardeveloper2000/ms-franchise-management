package com.epam.franquicias.infrastructure.adapter.in.web;

import com.epam.franquicias.application.port.in.ActualizarNombreFranquiciaUseCase;
import com.epam.franquicias.application.port.in.ActualizarNombreProductoUseCase;
import com.epam.franquicias.application.port.in.ActualizarNombreSucursalUseCase;
import com.epam.franquicias.application.port.in.AgregarFranquiciaUseCase;
import com.epam.franquicias.application.port.in.AgregarProductoUseCase;
import com.epam.franquicias.application.port.in.AgregarSucursalUseCase;
import com.epam.franquicias.application.port.in.EliminarProductoUseCase;
import com.epam.franquicias.application.port.in.ModificarStockUseCase;
import com.epam.franquicias.application.port.in.ObtenerProductoMasStockPorSucursalUseCase;
import jakarta.validation.Validation;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.mockito.Mockito.mock;

class FranquiciaRouterConfigTest {

    @Test
    void shouldReturnUpStatusFromHealthEndpoint() {
        FranquiciaHandler handler = new FranquiciaHandler(
                mock(AgregarFranquiciaUseCase.class),
                mock(AgregarSucursalUseCase.class),
                mock(AgregarProductoUseCase.class),
                mock(EliminarProductoUseCase.class),
                mock(ModificarStockUseCase.class),
                mock(ObtenerProductoMasStockPorSucursalUseCase.class),
                mock(ActualizarNombreFranquiciaUseCase.class),
                mock(ActualizarNombreSucursalUseCase.class),
                mock(ActualizarNombreProductoUseCase.class),
                Validation.buildDefaultValidatorFactory().getValidator());
        RouterFunction<ServerResponse> routes = new FranquiciaRouterConfig().franquiciaRoutes(handler);

        WebTestClient.bindToRouterFunction(routes)
                .build()
                .get()
                .uri("/health")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .json("{\"status\":\"UP\"}");
    }
}
