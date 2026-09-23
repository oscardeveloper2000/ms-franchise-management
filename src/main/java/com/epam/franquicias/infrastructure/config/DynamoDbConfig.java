package com.epam.franquicias.infrastructure.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.regions.Region;

@Configuration
public class DynamoDbConfig {

    @Bean
    public DynamoDbAsyncClient dynamoDbAsyncClient(
            @Value("${aws.region:us-east-1}") String region) {
        return DynamoDbAsyncClient.builder()
                .region(Region.of(region))
                .build();
    }

    @Bean
    public DynamoDbEnhancedAsyncClient dynamoDbEnhancedAsyncClient(DynamoDbAsyncClient dynamoDbAsyncClient) {
        return DynamoDbEnhancedAsyncClient.builder()
                .dynamoDbClient(dynamoDbAsyncClient)
                .build();
    }

    @Bean(name = "tableName")
    @Qualifier("tableName")
    public String tableName(@Value("${aws.dynamodb.table-name}") String tableName) {
        return tableName;
    }
}