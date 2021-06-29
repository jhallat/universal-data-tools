package com.jhallat.universaldatatools.relationaldb.definition;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ColumnDef(@JsonProperty("name") String name,
                        @JsonProperty("defaultValue") String defaultValue,
                        @JsonProperty("isNullable") boolean isNullable,
                        @JsonProperty("dataType") String dataType,
                        @JsonProperty("maxLength") int maxLength,
                        @JsonProperty("numericPrecision") int numericPrecision,
                        @JsonProperty("numericScale") int numericScale,
                        @JsonProperty("updatable") boolean updatable) {
}
