package com.epam.franquicias.application.usecase;

import com.epam.franquicias.application.exception.FranquiciaNoEncontradaException;
import com.epam.franquicias.application.exception.SucursalNoEncontradaException;
import com.epam.franquicias.application.port.in.ActualizarNombreSucursalUseCase;
import com.epam.franquicias.application.port.out.FranquiciaRepositoryPort;
import com.epam.franquicias.domain.model.Franquicia;
import com.epam.franquicias.domain.model.Sucursal;
import java.util.Objects;
import java.util.UUID;
import reactor.core.publisher.Mono;

public class ActualizarNombreSucursalUseCaseImpl implements ActualizarNombreSucursalUseCase {

    private final FranquiciaRepositoryPort franquiciaRepository;

    public ActualizarNombreSucursalUseCaseImpl(FranquiciaRepositoryPort franquiciaRepository) {
        this.franquiciaRepository = franquiciaRepository;
    }

    @Override
    public Mono<Franquicia> ejecutar(UUID franquiciaId, UUID sucursalId, String nuevoNombre) {
        return franquiciaRepository.buscarPorId(franquiciaId)
                .switchIfEmpty(Mono.error(() -> new FranquiciaNoEncontradaException(franquiciaId)))
                .flatMap(franquicia -> {
                    Sucursal sucursal = franquicia.getSucursales().stream()
                            .filter(sucursalEncontrada -> Objects.equals(sucursalEncontrada.getId(), sucursalId))
                            .findFirst()
                            .orElseThrow(() -> new SucursalNoEncontradaException(sucursalId));

                    sucursal.actualizarNombre(nuevoNombre);
                    return franquiciaRepository.guardar(franquicia);
                });
    }
}