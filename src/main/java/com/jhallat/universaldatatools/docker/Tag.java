package com.jhallat.universaldatatools.docker;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Tag(@JsonProperty("level") String level, @JsonProperty("name") String name) {
}
