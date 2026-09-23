package com.epam.franquicias.application.port.in;

import java.util.UUID;
import reactor.core.publisher.Flux;

public interface ObtenerProductoMasStockPorSucursalUseCase {

    Flux<ProductoPorSucursal> ejecutar(UUID franquiciaId);
}