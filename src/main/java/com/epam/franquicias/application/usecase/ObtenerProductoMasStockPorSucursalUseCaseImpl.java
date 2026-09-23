package com.epam.franquicias.application.usecase;

import com.epam.franquicias.application.exception.FranquiciaNoEncontradaException;
import com.epam.franquicias.application.port.in.ObtenerProductoMasStockPorSucursalUseCase;
import com.epam.franquicias.application.port.in.ProductoPorSucursal;
import com.epam.franquicias.application.port.out.FranquiciaRepositoryPort;
import com.epam.franquicias.domain.model.Franquicia;
import java.util.Comparator;
import java.util.UUID;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class ObtenerProductoMasStockPorSucursalUseCaseImpl
        implements ObtenerProductoMasStockPorSucursalUseCase {

    private final FranquiciaRepositoryPort franquiciaRepository;

    public ObtenerProductoMasStockPorSucursalUseCaseImpl(FranquiciaRepositoryPort franquiciaRepository) {
        this.franquiciaRepository = franquiciaRepository;
    }

    @Override
    public Flux<ProductoPorSucursal> ejecutar(UUID franquiciaId) {
        Mono<Franquicia> franquicia = franquiciaRepository.buscarPorId(franquiciaId)
                .switchIfEmpty(Mono.error(() -> new FranquiciaNoEncontradaException(franquiciaId)));

        return franquicia.flatMapMany(franquiciaEncontrada -> Flux.fromStream(
                franquiciaEncontrada.getSucursales().stream()
                        .flatMap(sucursal -> sucursal.getProductos().stream()
                                .max(Comparator.comparingInt(producto -> producto.getCantidadStock()))
                                .stream()
                                .map(producto -> new ProductoPorSucursal(
                                        sucursal.getId(), sucursal.getNombre(), producto)))));
    }
}