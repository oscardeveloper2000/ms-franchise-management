package com.epam.franquicias.application.usecase;

import com.epam.franquicias.application.port.in.AgregarFranquiciaUseCase;
import com.epam.franquicias.application.port.out.FranquiciaRepositoryPort;
import com.epam.franquicias.domain.model.Franquicia;
import java.util.ArrayList;
import java.util.UUID;
import reactor.core.publisher.Mono;

public class AgregarFranquiciaUseCaseImpl implements AgregarFranquiciaUseCase {

    private final FranquiciaRepositoryPort franquiciaRepository;

    public AgregarFranquiciaUseCaseImpl(FranquiciaRepositoryPort franquiciaRepository) {
        this.franquiciaRepository = franquiciaRepository;
    }

    @Override
    public Mono<Franquicia> ejecutar(String nombre) {
        return Mono.defer(() -> {
            Franquicia franquicia = new Franquicia(UUID.randomUUID(), nombre, new ArrayList<>());
            return franquiciaRepository.guardar(franquicia);
        });
    }
}