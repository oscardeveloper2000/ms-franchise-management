package com.epam.franquicias.infrastructure.adapter.in.web.dto;

import com.epam.franquicias.domain.model.Sucursal;
import java.util.List;
import java.util.UUID;

public record SucursalResponse(UUID id, String nombre, List<ProductoResponse> productos) {

    public static SucursalResponse toResponse(Sucursal sucursal) {
        List<ProductoResponse> productos = sucursal.getProductos() == null
                ? List.of()
                : sucursal.getProductos().stream()
                        .map(ProductoResponse::toResponse)
                        .toList();

        return new SucursalResponse(sucursal.getId(), sucursal.getNombre(), productos);
    }
}
