package com.jhallat.universaldatatools.relationaldb.definition;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jhallat.universaldatatools.relationaldb.TableDescription;
import com.jhallat.universaldatatools.relationaldb.ViewDescription;

import java.util.List;

public record DatabaseDef(@JsonProperty("name") String name,
                          @JsonProperty("tables") List<TableDescription> tables,
                          @JsonProperty("views") List<ViewDescription> views) {}
