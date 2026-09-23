package com.epam.franquicias.application.port.in;

import com.epam.franquicias.domain.model.Franquicia;
import java.util.UUID;
import reactor.core.publisher.Mono;

public interface AgregarProductoUseCase {

    Mono<Franquicia> ejecutar(UUID franquiciaId, UUID sucursalId, String nombreProducto, int cantidadStock);
}