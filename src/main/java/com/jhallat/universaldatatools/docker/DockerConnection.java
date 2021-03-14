package com.jhallat.universaldatatools.docker;

import com.github.dockerjava.api.DockerClient;
import com.jhallat.universaldatatools.activeconnection.ActiveConnection;
import com.jhallat.universaldatatools.connectiondefinitions.ConnectionLabel;

public class DockerConnection extends ActiveConnection {

    private DockerClient dockerClient;

    @Override
    public ConnectionLabel getActiveConnectionType() {
        return ConnectionLabel.DOCKER;
    }

     public DockerConnection(DockerClient dockerClient) {
        this.dockerClient = dockerClient;
     }

    public DockerClient getDockerClient() {
        return dockerClient;
    }
}
