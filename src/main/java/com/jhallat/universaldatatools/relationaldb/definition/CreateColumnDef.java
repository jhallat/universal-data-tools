package com.jhallat.universaldatatools.relationaldb.definition;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CreateColumnDef(@JsonProperty("name") String name,
                              @JsonProperty("dataType") String dataType,
                              @JsonProperty("size") int size,
                              @JsonProperty("primaryKey") boolean primaryKey,
                              @JsonProperty("notNull") boolean notNull,
                              @JsonProperty("unique") boolean unique) {}
