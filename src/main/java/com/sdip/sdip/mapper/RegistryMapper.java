package com.sdip.sdip.mapper;




import com.sdip.sdip.config.RegistryConfigLoader;
import com.sdip.sdip.model.RegistryResponseDTO;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

//@Component
//public class RegistryMapper {
//
//    public RegistryResponseDTO map(String registryKey, RegistryConfigLoader.RegistryMeta meta, Map<String, Object> rawData) {
//        // Normalize ID
//
//        System.out.println(rawData);
//        String id = rawData.get(meta.getIdField()).toString();
//
//        // Example transformation logic
//        Map<String, Object> transformed = new HashMap<>();
//        transformed.put("registryName", meta.getName());
//        transformed.put("recordId", id);
//        transformed.put("attributes", rawData); // You can clean/rename here
//
//        return new RegistryResponseDTO(id, transformed);
//    }


@Component
public class RegistryMapper {

    public RegistryResponseDTO map(String registryKey, RegistryConfigLoader.RegistryMeta meta, Map<String, Object> rawData) {
        String id = rawData.get(meta.getIdField()).toString();

        // Copy and remove ID from the map to avoid duplication
        Map<String, Object> flattened = new HashMap<>(rawData);
        flattened.remove(meta.getIdField()); // Remove the ID to avoid double entry

        return new RegistryResponseDTO(id, flattened);
    }
}





