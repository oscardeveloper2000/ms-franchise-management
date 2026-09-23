package com.epam.franquicias.infrastructure.adapter.in.web.dto;

import com.epam.franquicias.domain.model.Producto;
import java.util.UUID;

public record ProductoResponse(UUID id, String nombre, int cantidadStock) {

    public static ProductoResponse toResponse(Producto producto) {
        return new ProductoResponse(producto.getId(), producto.getNombre(), producto.getCantidadStock());
    }
}
