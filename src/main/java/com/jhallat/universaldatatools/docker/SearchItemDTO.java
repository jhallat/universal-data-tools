package com.jhallat.universaldatatools.docker;

import lombok.Data;

@Data
public class SearchItemDTO {
    private String name;
    private String description;
    private int stars;
    private boolean official;
}
