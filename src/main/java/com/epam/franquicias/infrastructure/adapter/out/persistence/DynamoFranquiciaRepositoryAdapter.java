package com.epam.franquicias.infrastructure.adapter.out.persistence;

import com.epam.franquicias.application.port.out.FranquiciaRepositoryPort;
import com.epam.franquicias.domain.model.Franquicia;
import com.epam.franquicias.infrastructure.adapter.out.persistence.entity.FranquiciaEntity;
import com.epam.franquicias.infrastructure.adapter.out.persistence.mapper.FranquiciaEntityMapper;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Qualifier;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import org.springframework.stereotype.Repository;

@Repository
public class DynamoFranquiciaRepositoryAdapter implements FranquiciaRepositoryPort {

    private final DynamoDbAsyncTable<FranquiciaEntity> franquiciaTable;

    public DynamoFranquiciaRepositoryAdapter(DynamoDbEnhancedAsyncClient enhancedClient,
            @Qualifier("tableName") String tableName) {
        this.franquiciaTable = enhancedClient.table(tableName, TableSchema.fromBean(FranquiciaEntity.class));
    }

    @Override
    public Mono<Franquicia> guardar(Franquicia franquicia) {
        FranquiciaEntity entity = FranquiciaEntityMapper.toEntity(franquicia);
        return Mono.fromFuture(franquiciaTable.putItem(entity))
                .thenReturn(franquicia);
    }

    @Override
    public Mono<Franquicia> buscarPorId(UUID id) {
        Key key = Key.builder()
                .partitionValue(id.toString())
                .build();
        return Mono.fromFuture(franquiciaTable.getItem(key))
                .map(FranquiciaEntityMapper::toDomain);
    }
}