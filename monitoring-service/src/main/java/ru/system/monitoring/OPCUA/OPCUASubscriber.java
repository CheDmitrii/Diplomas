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
import ru.system.library.dto.common.sensor.SensorDTO;
import ru.system.library.dto.common.sensor.SensorJournalEntityDTO;
import ru.system.library.exception.HttpResponseEntityException;
import ru.system.monitoring.repository.repository.SensorRepository;
import ru.system.monitoring.service.JournalService;

import java.sql.Timestamp;
import java.util.HashMap;
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
    private final JournalService journalService;
//    private final SimpMessagingTemplate messagingTemplate;

    private OpcUaClient client;
    private UaSubscription subscription;
    private UShort sensorsNameSpaceIndex;
    private NodeId objectId;
    private NodeId methodCheckSensorsId;
    private NodeId methodCheckAllSensorsId;
    private NodeId methodCreateSensorId;
    private final ObjectMapper mapper = new ObjectMapper();

    @Value("${spring.opc.server.host}")
    private String serverHost;
    @Value("${spring.opc.uri.sensors}")
    private String sensorOPCUri;

    private Consumer<DataValue> sensorConsumer = value -> {
        Map<String, Object> map = null;
        try { // todo add messaging and db saving
            SensorJournalEntityDTO sensorJournal = mapper.readValue(value.getValue().getValue().toString(), SensorJournalEntityDTO.class);
            // todo uncommit after drop kafka
            //journalService.saveJournal(sensorJournal);
            //messagingTemplate.convertAndSend("/topic/journal" + sensorJournal.getId(), sensorJournal);

            map = mapper.readValue(value.getValue().getValue().toString(), new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            log.error("{}", e.getMessage());
        }

        if (map != null && map.containsKey("time")) { // time mapping good
            log.error("Map is not null");
            Timestamp time = Timestamp.valueOf((String) map.get("time"));
            log.error("time is {}", time);
        }
    };

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
        log.info("connection OPC UA state {}", client.connect().get().getSession().state());
        this.subscription = client.getSubscriptionManager().createSubscription(100.0).get();
        this.sensorsNameSpaceIndex = client.getNamespaceTable().getIndex(sensorOPCUri);
        log.error("Index of {} namespace is  {}", this.sensorOPCUri, sensorsNameSpaceIndex);

        this.configureSensorsNodeId();
        this.configureMethodsNodeId();
    }

    public SensorCheckedDTO[] checkSensors() {
        CallMethodRequest request = new CallMethodRequest(this.objectId, this.methodCheckAllSensorsId, null);
        CallMethodResult result = this.getResult(request);
        log.info("check all sensors response from OPC UA server {}", result);
        try {
            return mapper.readValue((String) result.getOutputArguments()[0].getValue(), SensorCheckedDTO[].class);
        } catch (JsonProcessingException e) {
            throw new HttpResponseEntityException(HttpStatus.INTERNAL_SERVER_ERROR, "Json parser error");
        }
    }

    public SensorCheckedDTO[] checkSensors(List<UUID> sensors) {
        Variant inputVariant = new Variant(sensors.stream().map(UUID::toString).toArray(String[]::new));
        CallMethodRequest request = new CallMethodRequest(this.objectId, this.methodCheckSensorsId, new Variant[]{inputVariant});
        CallMethodResult result = this.getResult(request);
        log.info("check sensors response from OPC UA server {}", result);
        try {
            return mapper.readValue((String) result.getOutputArguments()[0].getValue(), SensorCheckedDTO[].class);
        } catch (JsonProcessingException e) {
            throw new HttpResponseEntityException(HttpStatus.INTERNAL_SERVER_ERROR, "Json parser error");
        }
    }

    public void createSensor(SensorDTO sensor) {
        Map<String, String> map = new HashMap<>(Map.of(
                "id", sensor.getId().toString(),
                "name", sensor.getName(),
                "status", "good"
        ));
        if (sensor.getReference() != null) {
            map.put("max-value", sensor.getReference().getValue().toString());
        }
        Variant inputVariant = null;
        try {
            inputVariant = new Variant(mapper.writeValueAsString(map));
        } catch (JsonProcessingException e) {
            throw new HttpResponseEntityException(HttpStatus.INTERNAL_SERVER_ERROR, "Json parser error");
        }
        CallMethodRequest request = new CallMethodRequest(this.objectId, this.methodCreateSensorId, new Variant[]{inputVariant});
        var result = this.getResult(request);
        log.info("creating sensor response from OPC UA server {}", result);
        if (result.getStatusCode().isGood()) {
            log.info("{} sensor created", sensor.getId());
            this.configureSensorNode(sensor.getId());
        } else {
            log.error("{} sensor creation failed", sensor.getId());
        }
    }

    public CallMethodResult getResult(CallMethodRequest request) {
        if (request == null) {
            throw new HttpResponseEntityException(HttpStatus.INTERNAL_SERVER_ERROR, "Bad OPC UA request");
        }
        try {
            return this.client.call(request).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new HttpResponseEntityException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    public void configureSensorsNodeId() {
        sensorRepository.getAllSensorsIds().forEach(this::configureSensorNode);
    }

    public void configureSensorNode(UUID sensorId) {
        NodeId nodeId = new NodeId(sensorsNameSpaceIndex, sensorId.toString());
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
                    (item, status) -> log.info("Monitored item of sensor {} status: {}", sensorId, StatusCode.GOOD.getValue() == status)
            ).get().getFirst();
            monitoredItem.setValueConsumer(sensorConsumer);
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public void configureMethodsNodeId() {
        if (this.sensorsNameSpaceIndex == null) {
            log.error("Methods nodeId doesn't configure as sensorsNameSpaceIndex is null");
            return;
        }
        this.objectId = new NodeId(sensorsNameSpaceIndex, "Methods");
        this.methodCheckSensorsId = new NodeId(sensorsNameSpaceIndex, "CheckSensors");
        this.methodCheckAllSensorsId = new NodeId(sensorsNameSpaceIndex, "CheckAllSensors");
        this.methodCreateSensorId = new NodeId(sensorsNameSpaceIndex, "CreateSensor");
    }

    @PreDestroy
    public void stop() throws Exception {
        if (client != null) {
            client.disconnect().get();
        }
    }
}