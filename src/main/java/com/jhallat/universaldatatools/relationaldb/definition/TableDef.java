package com.jhallat.universaldatatools.relationaldb.definition;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record TableDef(@JsonProperty("name") String name,
                       @JsonProperty("string") String schema,
                       @JsonProperty("database") String database,
                       @JsonProperty("columns") List<ColumnDef> columns,
                       @JsonProperty("primaryKey") String primaryKey,
                       @JsonProperty("rows") List<List<String>> rows) {}
