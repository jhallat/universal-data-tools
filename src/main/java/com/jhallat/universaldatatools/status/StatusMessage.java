package com.jhallat.universaldatatools.status;

import com.fasterxml.jackson.annotation.JsonProperty;

public record StatusMessage(@JsonProperty("subject") String subject, @JsonProperty("message") String message) {}
