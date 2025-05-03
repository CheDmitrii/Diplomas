package ru.system.monitoring.config.OpcUa;

import lombok.RequiredArgsConstructor;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.api.config.OpcUaClientConfig;
import org.eclipse.milo.opcua.sdk.client.api.config.OpcUaClientConfigBuilder;
import org.eclipse.milo.opcua.sdk.client.api.identity.UsernameProvider;
import org.eclipse.milo.opcua.sdk.client.api.subscriptions.UaSubscription;
import org.eclipse.milo.opcua.stack.client.DiscoveryClient;
import org.eclipse.milo.opcua.stack.core.types.structured.EndpointDescription;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;


//@Configuration
@EnableConfigurationProperties(OpcUaConfigProperties.class)
@RequiredArgsConstructor
public class OpcUaConfig {
    private final OpcUaConfigProperties configProperties;

//    @Bean
    public OpcUaClient createClient() throws Exception {
        List<EndpointDescription> endpoints = DiscoveryClient.getEndpoints(configProperties.getEndpointUri()).get();
        EndpointDescription endpoint = endpoints.stream()
                .findFirst()
                .orElseThrow(() -> new Exception("Endpoint not found"));

        OpcUaClientConfigBuilder configBuilder = OpcUaClientConfig.builder()
                .setEndpoint(endpoint);


        OpcUaClient client = OpcUaClient.create(configBuilder.build());
        client.connect().get();

        return client;
    }

//    @Bean
    public UaSubscription createSubscription(OpcUaClient client) throws Exception {
        return client.getSubscriptionManager()
                .createSubscription(configProperties.getPublishingInterval()).get();
    }
}
