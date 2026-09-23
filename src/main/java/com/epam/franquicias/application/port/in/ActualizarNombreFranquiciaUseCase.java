package com.epam.franquicias.application.port.in;

import com.epam.franquicias.domain.model.Franquicia;
import java.util.UUID;
import reactor.core.publisher.Mono;

public interface ActualizarNombreFranquiciaUseCase {

    Mono<Franquicia> ejecutar(UUID franquiciaId, String nuevoNombre);
}