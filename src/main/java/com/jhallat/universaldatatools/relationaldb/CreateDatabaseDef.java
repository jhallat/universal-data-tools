package com.jhallat.universaldatatools.relationaldb;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CreateDatabaseDef(@JsonProperty("name") String name,
                                @JsonProperty("owner") String owner) {


}
