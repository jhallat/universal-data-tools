package com.jhallat.universaldatatools.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.jhallat.universaldatatools.activeconnection.ActiveConnection;
import com.jhallat.universaldatatools.activeconnection.ActiveConnectionFactory;
import com.jhallat.universaldatatools.connectiondefinitions.entities.ConnectionToken;

public class DockerConnectionFactory implements ActiveConnectionFactory {

    @Override
    public ActiveConnection createConnection(ConnectionToken connectionToken) {
        DefaultDockerClientConfig.Builder configBuilder = DefaultDockerClientConfig.createDefaultConfigBuilder();
        DockerClient dockerClient = DockerClientBuilder.getInstance().build();
        return new DockerConnection(dockerClient);
    }
}
