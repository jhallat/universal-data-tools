package com.jhallat.universaldatatools.docker;

import com.github.dockerjava.api.DockerClient;
import com.jhallat.universaldatatools.activeconnection.ActiveConnection;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class DockerConnection extends ActiveConnection {

    private final DockerClient dockerClient;

    @Override
    public String getLabel() {
        return DockerConfiguration.LABEL_DOCKER;
    }

    @Override
    public void close() {
        try {
            dockerClient.close();
        } catch (IOException e) {
            log.warn("Docker client did not close properly");
        }
    }

    public DockerConnection(DockerClient dockerClient) {
        this.dockerClient = dockerClient;
     }

    public DockerClient getDockerClient() {
        return dockerClient;
    }
}
