package com.jhallat.universaldatatools.connectiondefinitions;

import lombok.*;

import java.util.Map;

@Data
@AllArgsConstructor
public class ConnectionToken {

    private String token;
    private ConnectionLabel label;
    private String description;
    private boolean valid;
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private Map<String, String> propertyMap;

    public void getProperty(String property) {
        propertyMap.getOrDefault(property, "");
    }

    public void setProperty(String property, String value) {
        propertyMap.put(property, value);
    }
}
