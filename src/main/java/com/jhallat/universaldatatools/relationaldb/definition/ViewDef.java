package com.jhallat.universaldatatools.relationaldb.definition;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ViewDef(@JsonProperty("name") String name) {
}
