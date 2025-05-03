package ru.system.monitoring.OPCUA;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.api.config.OpcUaClientConfig;
import org.eclipse.milo.opcua.sdk.client.api.subscriptions.UaMonitoredItem;
import org.eclipse.milo.opcua.sdk.client.api.subscriptions.UaSubscription;
import org.eclipse.milo.opcua.stack.client.DiscoveryClient;
import org.eclipse.milo.opcua.stack.core.AttributeId;
import org.eclipse.milo.opcua.stack.core.security.SecurityPolicy;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.QualifiedName;
import org.eclipse.milo.opcua.stack.core.types.enumerated.MonitoringMode;
import org.eclipse.milo.opcua.stack.core.types.enumerated.TimestampsToReturn;
import org.eclipse.milo.opcua.stack.core.types.structured.EndpointDescription;
import org.eclipse.milo.opcua.stack.core.types.structured.MonitoredItemCreateRequest;
import org.eclipse.milo.opcua.stack.core.types.structured.MonitoringParameters;
import org.eclipse.milo.opcua.stack.core.types.structured.ReadValueId;
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
        client.connect().get();
//        client.getStaticDataTypeManager().registerCodec("Current Time",1, St);

        subscription = client.getSubscriptionManager()
                .createSubscription(1000.0).get();

        NodeId nodeId = new NodeId(1, "current-time");
        UaMonitoredItem monitoredItem = subscription.createMonitoredItems(
                TimestampsToReturn.Both,
                Collections.singletonList(new MonitoredItemCreateRequest(
                        new ReadValueId(nodeId, AttributeId.Value.uid(), null, null),
                        MonitoringMode.Reporting,
                        new MonitoringParameters(uint(1), 1000.0, null, uint(10), true)
                )),
                (item, value) -> {
                    log.error("Monitored item created: {}", value);
                    System.out.println("Value changed: " + value);
                }
        ).get().get(0);
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