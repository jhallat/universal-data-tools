package com.jhallat.universaldatatools.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;
import com.jhallat.universaldatatools.activeconnection.ActiveConnection;
import com.jhallat.universaldatatools.activeconnection.ActiveConnectionFactory;
import com.jhallat.universaldatatools.connectiondefinitions.ConnectionToken;

public class DockerConnectionFactory implements ActiveConnectionFactory {

    @Override
    public ActiveConnection createConnection(ConnectionToken connectionToken) {
        DefaultDockerClientConfig.Builder configBuilder = DefaultDockerClientConfig.createDefaultConfigBuilder();


        DockerClient dockerClient = DockerClientBuilder.getInstance().build();
        DockerConnection connection = new DockerConnection(dockerClient);
        return connection;
    }
}
