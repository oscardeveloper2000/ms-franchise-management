package com.epam.franquicias.infrastructure.adapter.in.web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class FranquiciaRouterConfig {

    @Bean
    public RouterFunction<ServerResponse> franquiciaRoutes(FranquiciaHandler handler) {
        return RouterFunctions.route()
                .path("/franquicias", builder -> builder
                        .POST("", handler::crearFranquicia)
                        .POST("/{franquiciaId}/sucursales", handler::agregarSucursal)
                        .POST("/{franquiciaId}/sucursales/{sucursalId}/productos", handler::agregarProducto)
                        .DELETE("/{franquiciaId}/sucursales/{sucursalId}/productos/{productoId}",
                                handler::eliminarProducto)
                        .PATCH("/{franquiciaId}/sucursales/{sucursalId}/productos/{productoId}/stock",
                                handler::modificarStock)
                        .GET("/{franquiciaId}/productos/top-stock", handler::obtenerTopStock)
                        .PATCH("/{franquiciaId}", handler::actualizarNombreFranquicia)
                        .PATCH("/{franquiciaId}/sucursales/{sucursalId}", handler::actualizarNombreSucursal)
                        .PATCH("/{franquiciaId}/sucursales/{sucursalId}/productos/{productoId}",
                                handler::actualizarNombreProducto))
                .build();
    }
}
