package com.jhallat.universaldatatools.relationaldb;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TableDescription(@JsonProperty("name") String name,
                               @JsonProperty("schema") String schema) {}
