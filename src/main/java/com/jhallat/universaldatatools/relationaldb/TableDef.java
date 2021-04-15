package com.jhallat.universaldatatools.relationaldb;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TableDef(@JsonProperty("name") String name) {}
