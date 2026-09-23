package com.epam.franquicias.infrastructure.adapter.in.web.dto;

import jakarta.validation.constraints.PositiveOrZero;

public record ActualizarStockRequest(
        @PositiveOrZero(message = "La cantidad de stock no puede ser negativa") int cantidadStock) {
}
