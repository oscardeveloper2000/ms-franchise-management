package com.epam.franquicias.application.usecase;

import com.epam.franquicias.application.exception.FranquiciaNoEncontradaException;
import com.epam.franquicias.application.port.in.AgregarSucursalUseCase;
import com.epam.franquicias.application.port.out.FranquiciaRepositoryPort;
import com.epam.franquicias.domain.model.Franquicia;
import com.epam.franquicias.domain.model.Sucursal;
import java.util.ArrayList;
import java.util.UUID;
import reactor.core.publisher.Mono;

public class AgregarSucursalUseCaseImpl implements AgregarSucursalUseCase {

    private final FranquiciaRepositoryPort franquiciaRepository;

    public AgregarSucursalUseCaseImpl(FranquiciaRepositoryPort franquiciaRepository) {
        this.franquiciaRepository = franquiciaRepository;
    }

    @Override
    public Mono<Franquicia> ejecutar(UUID franquiciaId, String nombreSucursal) {
        return franquiciaRepository.buscarPorId(franquiciaId)
                .switchIfEmpty(Mono.error(() -> new FranquiciaNoEncontradaException(franquiciaId)))
                .flatMap(franquicia -> {
                    Sucursal sucursal = new Sucursal(UUID.randomUUID(), nombreSucursal, new ArrayList<>());
                    franquicia.getSucursales().add(sucursal);
                    return franquiciaRepository.guardar(franquicia);
                });
    }
}