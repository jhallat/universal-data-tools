package com.jhallat.universaldatatools.relationaldb;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record DatabaseDef(@JsonProperty("name") String name,
                          @JsonProperty("tables") List<TableDescription> tables,
                          @JsonProperty("views") List<ViewDescription> views) {}
