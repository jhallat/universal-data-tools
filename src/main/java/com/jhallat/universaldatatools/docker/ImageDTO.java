package com.jhallat.universaldatatools.docker;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ImageDTO(@JsonProperty("id") String id,
                       @JsonProperty("tags") String tags) {
}
