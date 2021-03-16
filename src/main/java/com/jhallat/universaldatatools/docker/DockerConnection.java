package com.jhallat.universaldatatools.docker;

import com.github.dockerjava.api.DockerClient;
import com.jhallat.universaldatatools.activeconnection.ActiveConnection;
import com.jhallat.universaldatatools.connectiondefinitions.ConnectionLabel;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class DockerConnection extends ActiveConnection {

    private DockerClient dockerClient;

    @Override
    public ConnectionLabel getActiveConnectionType() {
        return ConnectionLabel.DOCKER;
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
