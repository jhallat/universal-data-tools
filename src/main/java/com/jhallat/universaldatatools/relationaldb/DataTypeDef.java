package com.jhallat.universaldatatools.relationaldb;

import com.fasterxml.jackson.annotation.JsonProperty;

public record DataTypeDef(@JsonProperty("name") String name,
                          @JsonProperty("isLengthProvided") boolean isLengthProvided,
                          @JsonProperty("isScaleProvided") boolean isScaleProvided,
                          @JsonProperty("isPrecisionProvided") boolean isPrecisionProvided) {
}
