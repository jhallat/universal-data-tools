package com.jhallat.universaldatatools.docker;

import lombok.Data;

@Data
public class PublishedPort {
    private int privatePort;
    private int publicPort;

    public String getMapping() {
        if (privatePort > 0 && publicPort > 0) {
            return String.format("%s:%s", privatePort, publicPort);
        }
        return "";
    }
}
