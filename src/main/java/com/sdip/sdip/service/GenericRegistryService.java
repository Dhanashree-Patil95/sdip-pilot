package com.sdip.sdip.service;

import com.sdip.sdip.config.RegistryConfigLoader;
import com.sdip.sdip.mapper.RegistryMapper;
import com.sdip.sdip.model.RegistryResponseDTO;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class GenericRegistryService {

    private final RegistryConfigLoader configLoader;
    private final RegistryMapper mapper;
    private final RestTemplate restTemplate = new RestTemplate();

    public GenericRegistryService(RegistryConfigLoader configLoader, RegistryMapper mapper) {
        this.configLoader = configLoader;
        this.mapper = mapper;
    }

    public RegistryResponseDTO fetchById(String registryKey, String id) {
        RegistryConfigLoader.RegistryMeta config = configLoader.getRegistries().get(registryKey);
        if (config == null) throw new RuntimeException("Unknown registry");

        String url = config.getEndpoint() + "/" + id;
        Map<String, Object> response = restTemplate.getForObject(url, Map.class);
        return mapper.map(registryKey, config, response);
    }

//    public List<RegistryResponseDTO> search(String registryKey, String filter) {
//        RegistryConfigLoader.RegistryMeta config = configLoader.getRegistries().get(registryKey);
//        if (config == null) throw new RuntimeException("Unknown registry");
//
//        // Build the URL dynamically based on whether the filter is provided
//        String url = config.getEndpoint();
//        if (filter != null && !filter.isEmpty()) {
//            url += "?filter=" + filter;
//        }
//
//        // Prepare headers (you can make them dynamic if needed)
//        HttpHeaders headers = new HttpHeaders();
//        headers.set("x-hasura-admin-secret", "Dhanashree@95920"); // Replace with dynamic header value if necessary
//        HttpEntity<String> entity = new HttpEntity<>(headers);
//
//        // Make the API call
//        Map<String, List<Map<String, Object>>> responseMap = restTemplate.exchange(
//                url,
//                HttpMethod.POST,
//                entity,
//                new ParameterizedTypeReference<Map<String, List<Map<String, Object>>>>() {}).getBody();
//
//        if (responseMap == null || responseMap.isEmpty()) {
//            return Collections.emptyList();
//        }
//
//// Assuming there's only one key in the response map (e.g., "sdipPilot_Test")
//        List<Map<String, Object>> list = responseMap.values().stream().findFirst().orElse(Collections.emptyList());
//
//        // Map the response to DTOs
//        return list.stream()
//                .map(item -> mapper.map(registryKey, config, item))
//                .collect(Collectors.toList());
//    }


public List<RegistryResponseDTO> search(String registryKey, Map<String, Object> filterMap) {
    RegistryConfigLoader.RegistryMeta config = configLoader.getRegistries().get(registryKey);
    if (config == null) {
        throw new RuntimeException("Unknown registry: " + registryKey);
    }

    String url = config.getEndpoint();

    if (filterMap == null) {
        filterMap = new HashMap<>();
    }

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.set("x-hasura-admin-secret", "Dhanashree@95920");

    HttpEntity<Map<String, Object>> request = new HttpEntity<>(filterMap, headers);

    ResponseEntity<Map<String, List<Map<String, Object>>>> response = restTemplate.exchange(
            url,
            HttpMethod.POST,
            request,
            new ParameterizedTypeReference<>() {}
    );

    Map<String, List<Map<String, Object>>> responseMap = response.getBody();
    if (responseMap == null || responseMap.isEmpty()) {
        return Collections.emptyList();
    }

    List<Map<String, Object>> dataList = responseMap.values().stream()
            .findFirst()
            .orElse(Collections.emptyList());

    return dataList.stream()
            .map(item -> mapper.map(registryKey, config, item))
            .collect(Collectors.toList());
}


    public boolean applyFilters(RegistryResponseDTO dto, String search, String filterField, Map<String, String> fieldFilters) {
        Map<String, Object> attributes = dto.getFlattenedAttributes();

        // Search on a particular field
        if (search != null && filterField != null) {
            Object value = attributes.get(filterField);
            if (value == null || !value.toString().toLowerCase().contains(search.toLowerCase())) {
                return false;
            }
        }

        // query.<fieldName>=value filters
        for (Map.Entry<String, String> entry : fieldFilters.entrySet()) {
            Object value = attributes.get(entry.getKey());
            if (value == null || !value.toString().equalsIgnoreCase(entry.getValue())) {
                return false;
            }
        }

        return true;
    }
    public Comparator<RegistryResponseDTO> getOrderingComparator(String orderingField) {
        if (orderingField == null || orderingField.isBlank()) {
            return Comparator.comparing(RegistryResponseDTO::getId); // fallback
        }

        return Comparator.comparing(dto -> {
            Object value = dto.getAttribute(orderingField);
            return value != null ? value.toString() : "";
        }, Comparator.naturalOrder());
    }



//
//public List<RegistryResponseDTO> search(
//        String registryKey,
//        Map<String, String> flatFilters,
//        String ordering,
//        int page,
//        int pageSize) {
//
//    RegistryConfigLoader.RegistryMeta config = configLoader.getRegistries().get(registryKey);
//    if (config == null) {
//        throw new RuntimeException("Unknown registry: " + registryKey);
//    }
//
//    String url = config.getEndpoint();
//
//    // Build the filter body in Hasura-compatible format
//    Map<String, Object> requestBody = new HashMap<>();
//    for (Map.Entry<String, String> entry : flatFilters.entrySet()) {
//        String field = entry.getKey();
//        String value = entry.getValue();
//
//        Map<String, String> condition = new HashMap<>();
//        // Apply _ilike if value contains % (wildcard), else use _eq
//        if (value.contains("%")) {
//            condition.put("_ilike", value);
//        } else {
//            condition.put("_eq", value);
//        }
//
//        requestBody.put(field, condition);
//    }
//
//    // Pagination logic (Hasura expects offset + limit)
//    requestBody.put("limit", pageSize);
//    requestBody.put("offset", (page - 1) * pageSize);
//
//    if (ordering != null) {
//        Map<String, String> orderMap = new HashMap<>();
//        orderMap.put("column", ordering.replace("-", "")); // strip '-' if exists
//        orderMap.put("order", ordering.startsWith("-") ? "desc" : "asc");
//        requestBody.put("order_by", orderMap);
//    }
//
//    HttpHeaders headers = new HttpHeaders();
//    headers.setContentType(MediaType.APPLICATION_JSON);
//    headers.set("x-hasura-admin-secret", "Dhanashree@95920");
//
//    HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
//
//    ResponseEntity<Map<String, List<Map<String, Object>>>> response = restTemplate.exchange(
//            url,
//            HttpMethod.POST,
//            request,
//            new ParameterizedTypeReference<>() {}
//    );
//
//    Map<String, List<Map<String, Object>>> responseMap = response.getBody();
//    if (responseMap == null || responseMap.isEmpty()) {
//        return Collections.emptyList();
//    }
//
//    List<Map<String, Object>> dataList = responseMap.values().stream()
//            .findFirst()
//            .orElse(Collections.emptyList());
//
//    return dataList.stream()
//            .map(item -> mapper.map(registryKey, config, item))
//            .collect(Collectors.toList());
//}


}
