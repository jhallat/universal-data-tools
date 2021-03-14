package com.jhallat.universaldatatools.docker;

import lombok.Data;

@Data
public class ContainerDTO {

    private String containerId;
    private String image;
    private String command;
    private String created;
    private String status;
    private String ports;
    private String names;

}
