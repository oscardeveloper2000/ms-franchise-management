package com.epam.franquicias.application.usecase;

import com.epam.franquicias.application.exception.FranquiciaNoEncontradaException;
import com.epam.franquicias.application.exception.SucursalNoEncontradaException;
import com.epam.franquicias.application.port.in.AgregarProductoUseCase;
import com.epam.franquicias.application.port.out.FranquiciaRepositoryPort;
import com.epam.franquicias.domain.model.Franquicia;
import com.epam.franquicias.domain.model.Producto;
import com.epam.franquicias.domain.model.Sucursal;
import java.util.UUID;
import reactor.core.publisher.Mono;

public class AgregarProductoUseCaseImpl implements AgregarProductoUseCase {

    private final FranquiciaRepositoryPort franquiciaRepository;

    public AgregarProductoUseCaseImpl(FranquiciaRepositoryPort franquiciaRepository) {
        this.franquiciaRepository = franquiciaRepository;
    }

    @Override
    public Mono<Franquicia> ejecutar(UUID franquiciaId, UUID sucursalId, String nombreProducto, int cantidadStock) {
        return franquiciaRepository.buscarPorId(franquiciaId)
                .switchIfEmpty(Mono.error(() -> new FranquiciaNoEncontradaException(franquiciaId)))
                .flatMap(franquicia -> {
                    Sucursal sucursal = franquicia.getSucursales().stream()
                            .filter(sucursalEncontrada -> sucursalEncontrada.getId().equals(sucursalId))
                            .findFirst()
                            .orElseThrow(() -> new SucursalNoEncontradaException(sucursalId));

                    Producto producto = new Producto(UUID.randomUUID(), nombreProducto, cantidadStock);
                    sucursal.getProductos().add(producto);
                    return franquiciaRepository.guardar(franquicia);
                });
    }
}