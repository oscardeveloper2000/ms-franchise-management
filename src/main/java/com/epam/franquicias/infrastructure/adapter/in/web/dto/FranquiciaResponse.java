package com.epam.franquicias.infrastructure.adapter.in.web.dto;

import com.epam.franquicias.domain.model.Franquicia;
import java.util.List;
import java.util.UUID;

public record FranquiciaResponse(UUID id, String nombre, List<SucursalResponse> sucursales) {

    public static FranquiciaResponse toResponse(Franquicia franquicia) {
        List<SucursalResponse> sucursales = franquicia.getSucursales() == null
                ? List.of()
                : franquicia.getSucursales().stream()
                        .map(SucursalResponse::toResponse)
                        .toList();

        return new FranquiciaResponse(franquicia.getId(), franquicia.getNombre(), sucursales);
    }
}
