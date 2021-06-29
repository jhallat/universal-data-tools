package com.jhallat.universaldatatools.relationaldb.definition;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record CreateTableDef(@JsonProperty("database") String database,
                             @JsonProperty("schema") String schema,
                             @JsonProperty("name") String name,
                             @JsonProperty("columns") List<CreateColumnDef> columns) {}
