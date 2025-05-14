package ru.system.monitoring.OPCUA;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.api.UaClient;
import org.eclipse.milo.opcua.sdk.client.api.config.OpcUaClientConfig;
import org.eclipse.milo.opcua.sdk.client.api.subscriptions.UaMonitoredItem;
import org.eclipse.milo.opcua.sdk.client.api.subscriptions.UaSubscription;
import org.eclipse.milo.opcua.stack.client.DiscoveryClient;
import org.eclipse.milo.opcua.stack.core.AttributeId;
import org.eclipse.milo.opcua.stack.core.security.SecurityPolicy;
import org.eclipse.milo.opcua.stack.core.serialization.SerializationContext;
import org.eclipse.milo.opcua.stack.core.types.builtin.*;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UInteger;
import org.eclipse.milo.opcua.stack.core.types.enumerated.DataChangeTrigger;
import org.eclipse.milo.opcua.stack.core.types.enumerated.DeadbandType;
import org.eclipse.milo.opcua.stack.core.types.enumerated.MonitoringMode;
import org.eclipse.milo.opcua.stack.core.types.enumerated.TimestampsToReturn;
import org.eclipse.milo.opcua.stack.core.types.structured.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

import static org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.uint;

@Component
@RequiredArgsConstructor
@Slf4j
public class OPCUASubscriber {
    private OpcUaClient client;
    private UaSubscription subscription;

    @PostConstruct
    public void start() throws Exception {
        List<EndpointDescription> endpoints = DiscoveryClient.getEndpoints("opc.tcp://localhost:4840").get();
        EndpointDescription endpoint = endpoints.stream()
                .filter(e -> e.getSecurityPolicyUri().equals(SecurityPolicy.None.getUri()))
                .findFirst()
                .orElseThrow(() -> new Exception("No endpoint found"));

        OpcUaClientConfig config = OpcUaClientConfig.builder()
                .setEndpoint(endpoint)
                .build();

        client = OpcUaClient.create(config);
        log.error("connection OPC UA state {}", client.connect().get().getSession().state());
//        client.sendRequest();

        subscription = client.getSubscriptionManager().createSubscription(100.0).get();
        log.error("{}", subscription.getRequestedPublishingInterval());

        NodeId nodeId = new NodeId(1, "data");
        // new QualifiedName(1, "Current Time")
        ReadValueId readValueId = new ReadValueId(nodeId, AttributeId.Value.uid(), null, null);

//        DataChangeFilter filter = new DataChangeFilter(
//                DataChangeTrigger.StatusValue,
//                UInteger.valueOf(DeadbandType.None.getValue()),
//                0.0
//        );
//        SerializationContext serializationContext = client.getStaticSerializationContext();
//        ExtensionObject filterExtension = ExtensionObject.encode(serializationContext, filter);

        MonitoringParameters parameters = new MonitoringParameters(
                UInteger.valueOf(1),
                100.0,
                null,
                uint(50),
                Boolean.TRUE
                );
        MonitoredItemCreateRequest monitoredItemCreateRequest = new MonitoredItemCreateRequest(readValueId, MonitoringMode.Reporting, parameters);
        UaMonitoredItem monitoredItem = subscription.createMonitoredItems(
                TimestampsToReturn.Both,
                List.of(monitoredItemCreateRequest),
                (item, status) -> {
                    log.info("Monitored item status: {}", StatusCode.GOOD.getValue() == status);
                }
        ).get().getFirst();
        log.error("{}", monitoredItem.getRequestedSamplingInterval());
        log.error("{}", monitoredItem.getRevisedSamplingInterval());

        monitoredItem.setValueConsumer(value -> log.info("value of node is {}", (String) value.getValue().getValue()));
    }

    @PreDestroy
    public void stop() throws Exception {
        if (subscription != null) {
//            subscription.delete(true).get();
        }
        if (client != null) {
            client.disconnect().get();
        }
    }
}