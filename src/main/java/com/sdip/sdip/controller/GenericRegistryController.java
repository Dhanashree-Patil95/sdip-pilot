package com.sdip.sdip.controller;

import com.sdip.sdip.model.RegistryResponseDTO;
import com.sdip.sdip.service.GenericRegistryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/data")
public class GenericRegistryController {

    private final GenericRegistryService service;

    public GenericRegistryController(GenericRegistryService service) {
        this.service = service;
    }

    @GetMapping("/{registry}/1.0/{id}")
    public ResponseEntity<RegistryResponseDTO> getById(
            @PathVariable String registry,
            @PathVariable String id) {
        RegistryResponseDTO dto = service.fetchById(registry, id);
        return ResponseEntity.ok(dto);
    }

//    @GetMapping("/{registry}/1.0/list")
//    public ResponseEntity<List<RegistryResponseDTO>> search(
//            @PathVariable String registry,
//            @RequestParam(defaultValue = "") String filter) {
//        List<RegistryResponseDTO> list = service.search(registry, filter);
//        return ResponseEntity.ok(list);
//    }


//    @GetMapping("/{registry}/1.0/list")
//    public ResponseEntity<List<RegistryResponseDTO>> search(
//            @PathVariable String registry,
//            @RequestParam Map<String, String> queryParams) {
//
//        // Reserved fields for pagination or ordering (optional)
//        String ordering = queryParams.remove("ordering");
//        int page = Integer.parseInt(queryParams.getOrDefault("page", "1"));
//        int pageSize = Integer.parseInt(queryParams.getOrDefault("pageSize", "10"));
//        queryParams.remove("page");
//        queryParams.remove("pageSize");
//
//        List<RegistryResponseDTO> results = service.search(
//                registry, queryParams, ordering, page, pageSize);
//
//        return ResponseEntity.ok(results);
//    }



//    @PostMapping("/{registry}/1.0/list")
//    public ResponseEntity<List<RegistryResponseDTO>> search(
//            @PathVariable String registry,
//            @RequestBody(required = false) Map<String, Object> filter) {
//
//        List<RegistryResponseDTO> results = service.search(registry, filter);
//        return ResponseEntity.ok(results);
//    }



    @GetMapping("/{registry}/1.0/list")
    public ResponseEntity<Map<String, Object>> search(
            @PathVariable String registry,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String filter,
            @RequestParam(required = false) String ordering,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int pageSize,
            @RequestParam Map<String, String> allRequestParams
    ) {
        // Extract query.<fieldName> filters
        Map<String, String> fieldFilters = allRequestParams.entrySet().stream()
                .filter(e -> e.getKey().startsWith("query."))
                .collect(Collectors.toMap(
                        e -> e.getKey().substring("query.".length()),
                        Map.Entry::getValue
                ));

        // Fetch raw data from internal Hasura endpoint
        List<RegistryResponseDTO> allResults = service.search(registry, null);

        // Filter and sort results
        List<RegistryResponseDTO> filteredResults = allResults.stream()
                .filter(dto -> service.applyFilters(dto, search, filter, fieldFilters))
                .sorted("descending".equalsIgnoreCase(ordering)
                        ? service.getOrderingComparator(filter).reversed()
                        : service.getOrderingComparator(filter))
                .collect(Collectors.toList());

        // Pagination
        int totalCount = filteredResults.size();
        int fromIndex = Math.min((page - 1) * pageSize, totalCount);
        int toIndex = Math.min(fromIndex + pageSize, totalCount);
        List<RegistryResponseDTO> pagedResults = filteredResults.subList(fromIndex, toIndex);

        // Prepare response map
        Map<String, Object> response = new HashMap<>();
        response.put("count", totalCount);
        response.put("next", toIndex < totalCount ? page + 1 : null);
        response.put("previous", page > 1 ? "https://example.com?page=" + (page - 1) : null);
        response.put("results", pagedResults);

        return ResponseEntity.ok(response);
    }

}

