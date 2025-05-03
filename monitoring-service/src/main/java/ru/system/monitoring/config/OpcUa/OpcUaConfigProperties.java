package ru.system.monitoring.config.OpcUa;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.opcua.client")
@Data
public class OpcUaConfigProperties {
    private String endpointUri;
    private Double publishingInterval;
}
