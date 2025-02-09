package com.manteigueiro.kafka;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.HashMap;
import java.util.Map;
import java.io.Serializable;

public class MDCContextModel implements Serializable {
    private final Map<String, String> contextMap;

    // Constructor needed for Jackson
    public MDCContextModel() {
        this.contextMap = new HashMap<>();
    }

    public MDCContextModel(Map<String, String> contextMap) {
        this.contextMap = contextMap != null ? new HashMap<>(contextMap) : new HashMap<>();
    }

    // Jackson creator for deserialization
    @JsonCreator
    public static MDCContextModel create(@JsonProperty("contextMap") Map<String, String> contextMap) {
        return new MDCContextModel(contextMap);
    }

    @JsonProperty("contextMap")
    public Map<String, String> getContextMap() {
        return contextMap;
    }
}