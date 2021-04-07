package com.jhallat.universaldatatools.docker;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
public class ContainerCreationDefinition {
    @NotBlank
    private String image;
    private String name;
    List<PublishedPort> publishedPorts;
}
