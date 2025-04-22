    package com.sdip.sdip.model;

    import com.fasterxml.jackson.annotation.JsonAnyGetter;

    import java.util.Map;

//    public class RegistryResponseDTO {
//        private String id;
//        private Map<String, Object> data;
//
//        public void setId(String id) {
//            this.id = id;
//        }
//
//        public RegistryResponseDTO(String id, Map<String, Object> data) {
//            this.id = id;
//            this.data = data;
//        }
//
//        public void setData(Map<String, Object> data) {
//            this.data = data;
//        }
//
//        public Map<String, Object> getData() {
//            return data;
//        }
//
//        public String getId() {
//            return id;
//        }
//    }

    // RegistryResponseDTO.java
//    public class RegistryResponseDTO {
//        private String id;
//        private Map<String, Object> attributes;
//
//        public RegistryResponseDTO(String id, Map<String, Object> attributes) {
//            this.id = id;
//            this.attributes = attributes;
//        }
//
//        public String getId() {
//            return id;
//        }
//
//        public Map<String, Object> getAttributes() {
//            return attributes;
//        }
//
//        public Object getAttribute(String key) {
//            return attributes != null ? attributes.get(key) : null;
//        }
//    }


    import com.fasterxml.jackson.annotation.JsonAnyGetter;
    import com.fasterxml.jackson.annotation.JsonIgnore;

    import java.util.Map;

    public class RegistryResponseDTO {

        private final String id;

        @JsonIgnore // Prevent it from being included in JSON output
        private final Map<String, Object> attributes;

        public RegistryResponseDTO(String id, Map<String, Object> attributes) {
            this.id = id;
            this.attributes = attributes;
        }

        public String getId() {
            return id;
        }

        @JsonAnyGetter // Flatten attributes into root level of JSON
        public Map<String, Object> getFlattenedAttributes() {
            return attributes;
        }

        public Object getAttribute(String key) {
            return attributes.get(key);
        }
    }




