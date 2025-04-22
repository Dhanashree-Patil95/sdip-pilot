package com.sdip.sdip.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@ConfigurationProperties(prefix = "registries")
@Data
public class RegistryConfigLoader {
    private Map<String, RegistryMeta> registries;

    @Data
    public static class RegistryMeta {
        private String name;
        private String endpoint;
        private String getByIdEndpoint;
        private String idField;
        private Map<String, String> headers;
    }
}
