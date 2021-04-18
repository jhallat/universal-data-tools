package com.jhallat.universaldatatools.relationaldb;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record TableDef(@JsonProperty("name") String name,
                       @JsonProperty("columns") List<ColumnDef> columns,
                       @JsonProperty("primaryKey") String primaryKey,
                       @JsonProperty("rows") List<List<String>> rows) {}
