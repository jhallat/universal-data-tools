package com.jhallat.universaldatatools.relationaldb;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ViewDef(@JsonProperty("name") String name) {
}
