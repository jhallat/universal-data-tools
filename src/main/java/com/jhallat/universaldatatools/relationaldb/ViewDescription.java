package com.jhallat.universaldatatools.relationaldb;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ViewDescription(@JsonProperty("name") String name,
                              @JsonProperty("schema") String schema) {
}
