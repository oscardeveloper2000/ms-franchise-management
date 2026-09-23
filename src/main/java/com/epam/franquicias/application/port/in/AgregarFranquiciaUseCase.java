package com.epam.franquicias.application.port.in;

import com.epam.franquicias.domain.model.Franquicia;
import reactor.core.publisher.Mono;

public interface AgregarFranquiciaUseCase {

    Mono<Franquicia> ejecutar(String nombre);
}