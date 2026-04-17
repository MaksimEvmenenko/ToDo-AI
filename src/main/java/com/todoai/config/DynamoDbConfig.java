package com.todoai.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.net.URI;

/**
 * DynamoDB Configuration
 * Configures DynamoDB client for connecting to local DynamoDB or AWS DynamoDB
 */
@Configuration
public class DynamoDbConfig {

    @Value("${spring.dynamodb.endpoint:http://localhost:8000}")
    private String dynamoDbEndpoint;

    @Value("${spring.dynamodb.region:us-east-1}")
    private String awsRegion;

    /**
     * Creates a DynamoDB client bean
     * @return DynamoDbClient configured for local or remote DynamoDB
     */
    @Bean
    public DynamoDbClient dynamoDbClient() {
        return DynamoDbClient.builder()
                .endpointOverride(URI.create(dynamoDbEndpoint))
                .region(Region.of(awsRegion))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }
}

