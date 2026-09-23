package com.epam.franquicias.application.port.in;

import com.epam.franquicias.domain.model.Producto;
import java.util.UUID;

public record ProductoPorSucursal(UUID sucursalId, String nombreSucursal, Producto producto) {
}