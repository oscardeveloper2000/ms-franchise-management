package com.epam.franquicias.application.port.out;

import com.epam.franquicias.domain.model.Franquicia;
import java.util.UUID;
import reactor.core.publisher.Mono;

public interface FranquiciaRepositoryPort {

    Mono<Franquicia> guardar(Franquicia franquicia);

    Mono<Franquicia> buscarPorId(UUID id);
}