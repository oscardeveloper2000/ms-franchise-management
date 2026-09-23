package com.epam.franquicias.application.usecase;

import com.epam.franquicias.application.exception.FranquiciaNoEncontradaException;
import com.epam.franquicias.application.port.in.ActualizarNombreFranquiciaUseCase;
import com.epam.franquicias.application.port.out.FranquiciaRepositoryPort;
import com.epam.franquicias.domain.model.Franquicia;
import java.util.UUID;
import reactor.core.publisher.Mono;

public class ActualizarNombreFranquiciaUseCaseImpl implements ActualizarNombreFranquiciaUseCase {

    private final FranquiciaRepositoryPort franquiciaRepository;

    public ActualizarNombreFranquiciaUseCaseImpl(FranquiciaRepositoryPort franquiciaRepository) {
        this.franquiciaRepository = franquiciaRepository;
    }

    @Override
    public Mono<Franquicia> ejecutar(UUID franquiciaId, String nuevoNombre) {
        return franquiciaRepository.buscarPorId(franquiciaId)
                .switchIfEmpty(Mono.error(() -> new FranquiciaNoEncontradaException(franquiciaId)))
                .flatMap(franquicia -> {
                    franquicia.actualizarNombre(nuevoNombre);
                    return franquiciaRepository.guardar(franquicia);
                });
    }
}