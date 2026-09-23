package com.epam.franquicias.infrastructure.adapter.in.web.dto;

import java.util.UUID;

public record ProductoPorSucursalResponse(UUID sucursalId, String nombreSucursal, ProductoResponse producto) {
}
