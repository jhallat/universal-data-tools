package com.jhallat.universaldatatools.relationaldb.definition;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CreateDatabaseDef(@JsonProperty("name") String name,
                                @JsonProperty("owner") String owner) {


}
