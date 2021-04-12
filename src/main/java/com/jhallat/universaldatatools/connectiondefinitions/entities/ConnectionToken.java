package com.jhallat.universaldatatools.connectiondefinitions.entities;

import lombok.*;

import java.util.Map;

@Data
@AllArgsConstructor
public class ConnectionToken {

    private String token;
    private String type;
    private String label;
    private String page;
    private String description;
    private boolean valid;
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private Map<Integer, String> propertyMap;

    public String getProperty(int property) {
        return propertyMap.getOrDefault(property, "");
    }

    public void setProperty(int property, String value) {
        propertyMap.put(property, value);
    }
}
