package com.jhallat.universaldatatools.relationaldb;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CreateColumnDef(@JsonProperty("name") String name,
                              @JsonProperty("datatype") String dataType,
                              @JsonProperty("size") int size,
                              @JsonProperty("primaryKey") boolean primaryKey,
                              @JsonProperty("notNull") boolean notNull,
                              @JsonProperty("unique") boolean unique) {}
