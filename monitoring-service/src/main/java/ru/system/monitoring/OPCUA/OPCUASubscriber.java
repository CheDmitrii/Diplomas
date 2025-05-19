package ru.system.monitoring.OPCUA;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.eclipse.milo.opcua.stack.core.types.builtin.*;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UInteger;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UShort;
import org.eclipse.milo.opcua.stack.core.types.enumerated.MonitoringMode;
import org.eclipse.milo.opcua.stack.core.types.enumerated.TimestampsToReturn;
import org.eclipse.milo.opcua.stack.core.types.structured.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import ru.system.library.dto.common.sensor.SensorCheckedDTO;
import ru.system.library.exception.HttpResponseEntityException;
import ru.system.monitoring.repository.repository.SensorRepository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

import static org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.uint;

@Component
@RequiredArgsConstructor
@Slf4j
public class OPCUASubscriber {

    private final SensorRepository sensorRepository;

    private OpcUaClient client;
    private UaSubscription subscription;
    private UShort sensorsNameSpaceIndex;
    private NodeId objectId;
    private NodeId methodCheckSensorsId;
    private NodeId methodCheckAllSensorsId;
    private final ObjectMapper mapper = new ObjectMapper();

    @Value("${spring.opc.server.host}")
    private String serverHost;
    @Value("${spring.opc.uri.sensors}")
    private String sensorOPCUri;

    @PostConstruct
    public void start() throws Exception {
        List<EndpointDescription> endpoints = DiscoveryClient.getEndpoints(serverHost).get();
        EndpointDescription endpoint = endpoints.stream()
                .filter(e -> e.getSecurityPolicyUri().equals(SecurityPolicy.None.getUri()))
                .findFirst()
                .orElseThrow(() -> new Exception("No endpoint found"));

        OpcUaClientConfig config = OpcUaClientConfig.builder()
                .setEndpoint(endpoint)
                .build();

        this.client = OpcUaClient.create(config);
        log.error("connection OPC UA state {}", client.connect().get().getSession().state());
        this.subscription = client.getSubscriptionManager().createSubscription(100.0).get();
        log.error("{}", subscription.getRequestedPublishingInterval());
        this.sensorsNameSpaceIndex = client.getNamespaceTable().getIndex(sensorOPCUri);
        log.error("Namespace index {}", sensorsNameSpaceIndex);


        this.configureSensorsNodeId();
        this.configureMethodsNodeId();















        // todo: next don't need
        NodeId nodeId = new NodeId(sensorsNameSpaceIndex, "976b3741-2f02-48fc-988c-53d86e3338fe"); // todo: implement for all sensors;
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
//        log.error("{}", monitoredItem.getRequestedSamplingInterval());
//        log.error("{}", monitoredItem.getRevisedSamplingInterval());

        monitoredItem.setValueConsumer(value -> {
            log.info("value of node is {}", value.getValue().getValue().toString());
            Map<String, Object> map = null;
            try {
                map = mapper.readValue(value.getValue().getValue().toString(), new TypeReference<Map<String, Object>>(){});
            } catch (Exception e) {
                log.error("{}", e.getMessage());
            }
            if (map != null && map.containsKey("time")) {
                log.error("Map is not null");
                Timestamp time = Timestamp.valueOf((String) map.get("time"));
                log.error("time is {}", time);
            }
        });
    }

    public SensorCheckedDTO[] checkSensors() throws ExecutionException, InterruptedException {
        CallMethodRequest request = new CallMethodRequest(this.objectId, this.methodCheckAllSensorsId, null);
        var result = client.call(request).get();
        try {
            return mapper.readValue((String) result.getOutputArguments()[0].getValue(), SensorCheckedDTO[].class);
        } catch (JsonProcessingException e) {
            throw new HttpResponseEntityException(HttpStatus.INTERNAL_SERVER_ERROR, "Json parser error");
        }
    }

    public SensorCheckedDTO[] checkSensors(List<UUID> sensors) throws ExecutionException, InterruptedException {
        Variant inputVariant = new Variant(sensors.stream().map(UUID::toString).toArray(String[]::new));
        CallMethodRequest request = new CallMethodRequest(this.objectId, this.methodCheckSensorsId, new Variant[]{inputVariant});
        var result = client.call(request).get();
        try {
            return mapper.readValue((String) result.getOutputArguments()[0].getValue(), SensorCheckedDTO[].class);
        } catch (JsonProcessingException e) {
            throw new HttpResponseEntityException(HttpStatus.INTERNAL_SERVER_ERROR, "Json parser error");
        }
    }




    public Integer getValue() throws ExecutionException, InterruptedException {
        NodeId objectId = new NodeId(sensorsNameSpaceIndex, "MyObjects");
        NodeId methodId = new NodeId(sensorsNameSpaceIndex, "GetAnswer");
        CallMethodRequest request = new CallMethodRequest(objectId, methodId, null);
        var result = client.call(request).get();
        return (Integer) result.getOutputArguments()[0].getValue();
    }

    public void configureSensorsNodeId() {
        Consumer<DataValue> consumer = value -> {
            Map<String, Object> map = null;
            try { // todo add messaging and db saving
                map = mapper.readValue(value.getValue().getValue().toString(), new TypeReference<Map<String, Object>>() {
                });
            } catch (Exception e) {
                log.error("{}", e.getMessage());
            }
            if (map != null && map.containsKey("time")) { // time mapping good
                log.error("Map is not null");
                Timestamp time = Timestamp.valueOf((String) map.get("time"));
                log.error("time is {}", time);
            }
        };
        sensorRepository.getAllSensorsIds()
                .forEach(
                        id -> {
                            NodeId nodeId = new NodeId(sensorsNameSpaceIndex, id.toString());
                            ReadValueId readValueId = new ReadValueId(nodeId, AttributeId.Value.uid(), null, null);
                            MonitoringParameters parameters = new MonitoringParameters(
                                    UInteger.valueOf(1),
                                    100.0,
                                    null,
                                    uint(50),
                                    Boolean.TRUE
                            );
                            MonitoredItemCreateRequest monitoredItemCreateRequest = new MonitoredItemCreateRequest(readValueId, MonitoringMode.Reporting, parameters);
                            try {
                                UaMonitoredItem monitoredItem = subscription.createMonitoredItems(
                                        TimestampsToReturn.Both,
                                        List.of(monitoredItemCreateRequest),
                                        (item, status) -> {
                                            log.info("Monitored item of sensor {} status: {}", id, StatusCode.GOOD.getValue() == status);
                                        }
                                ).get().getFirst();
                                monitoredItem.setValueConsumer(consumer);
                            } catch (InterruptedException | ExecutionException e) {
                                throw new RuntimeException(e);
                            }
                        }
                );
    }

    public void configureMethodsNodeId() {
        if (this.sensorsNameSpaceIndex == null) {
            log.error("Methods nodeId doesn't configure as sensorsNameSpaceIndex is null");
            return;
        }
        this.objectId = new NodeId(sensorsNameSpaceIndex, "Methods");
        this.methodCheckSensorsId = new NodeId(sensorsNameSpaceIndex, "CheckSensors");
        this.methodCheckAllSensorsId = new NodeId(sensorsNameSpaceIndex, "CheckAllSensors");
    }

    @PreDestroy
    public void stop() throws Exception {
//        if (subscription != null) {
////            subscription.delete(true).get();
//        }
        if (client != null) {
            client.disconnect().get();
        }
    }
}