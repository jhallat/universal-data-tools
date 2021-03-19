package com.jhallat.universaldatatools.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Container;
import com.jhallat.universaldatatools.activeconnection.ActiveConnection;
import com.jhallat.universaldatatools.activeconnection.ActiveConnectionService;
import com.jhallat.universaldatatools.exceptions.InvalidRequestException;
import com.jhallat.universaldatatools.exceptions.MissingConnectionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DockerService {

    private final ActiveConnectionService activeConnectionService;
    private final DockerMapper dockerMapper;

    private DockerClient findDockerClient(String connectionToken) throws MissingConnectionException {
        ActiveConnection activeConnection = activeConnectionService.getConnection(connectionToken);
        if (activeConnection instanceof DockerConnection connection) {
            return connection.getDockerClient();
        }
        log.warn("Expected DOCKER connection, found {}", activeConnection.getActiveConnectionType().name());
        throw new MissingConnectionException("Connection missing or expired.");
    }

    public List<ContainerDTO> getContainers(String connectionToken) throws MissingConnectionException {
        DockerClient client = findDockerClient(connectionToken);
        List<Container> containers = client.listContainersCmd().withShowAll(true).exec();
        return containers.stream()
                .map(dockerMapper::mapContainer)
                .collect(Collectors.toList());

    }

    public ContainerDTO startContainer(String connectionToken, String containerId) throws MissingConnectionException,
            InvalidRequestException {

        DockerClient client = findDockerClient(connectionToken);
        client.startContainerCmd(containerId).exec();
        List<Container> containers =
                client.listContainersCmd().withIdFilter(Collections.singletonList(containerId)).exec();
        if (containers.isEmpty()) {
            throw new InvalidRequestException(String.format("Container %s was not found", containerId));
        }
        return dockerMapper.mapContainer(containers.get(0));

    }

    public ContainerDTO stopContainer(String connectionToken, String containerId) throws MissingConnectionException,
            InvalidRequestException {

        DockerClient client = findDockerClient(connectionToken);
        client.stopContainerCmd(containerId).exec();
        List<Container> containers =
                client.listContainersCmd().withShowAll(true).withIdFilter(Collections.singletonList(containerId)).exec();
        if (containers.isEmpty()) {
            throw new InvalidRequestException(String.format("Container %s was not found", containerId));
        }
        return dockerMapper.mapContainer(containers.get(0));

    }
}
