package com.jhallat.universaldatatools.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.*;
import com.jhallat.universaldatatools.activeconnection.ActiveConnection;
import com.jhallat.universaldatatools.activeconnection.ActiveConnectionService;
import com.jhallat.universaldatatools.exceptions.InvalidRequestException;
import com.jhallat.universaldatatools.exceptions.MissingConnectionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
        log.warn("Expected DOCKER connection, found {} for token {}", activeConnection.getLabel(), connectionToken);
        throw new MissingConnectionException();
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

    public List<SearchItemDTO> searchImages(String connectionToken,
                                            String imageName,
                                            Boolean officialOnly,
                                            Integer minimumRating) throws MissingConnectionException {

        DockerClient client = findDockerClient(connectionToken);
        List<SearchItem> images = client.searchImagesCmd(imageName).exec();
        List<SearchItemDTO> dtos = images.stream()
                .map(dockerMapper::mapSearchItem)
                .collect(Collectors.toList());
        if (officialOnly != null && officialOnly) {
            dtos = dtos.stream().filter(item -> item.isOfficial()).collect(Collectors.toList());
        }
        if (minimumRating != null && minimumRating > 0) {
            dtos = dtos.stream().filter(item -> item.getStars() >= minimumRating).collect(Collectors.toList());
        }
        return dtos;
    }

    public ContainerDTO createContainer(String connectionToken, ContainerCreationDefinition definition)
            throws MissingConnectionException, InvalidRequestException {
        DockerClient client = findDockerClient(connectionToken);
        var command = client.createContainerCmd(definition.getImage());
        if (!StringUtils.isBlank(definition.getName())) {
            command.withName(definition.getName());
        }
        var hostConfig = HostConfig.newHostConfig();
        List<PortBinding> portBindings = new ArrayList<>()
;        for (PublishedPort port : definition.publishedPorts) {
            if (!StringUtils.isBlank(port.getMapping())) {
                portBindings.add(PortBinding.parse(port.getMapping()));
            }
        }
        if (!portBindings.isEmpty()) {
            hostConfig.withPortBindings(portBindings);
        }
        command.withHostConfig(hostConfig);

        var response = command.exec();
        List<Container> containers =
                client.listContainersCmd().withIdFilter(Collections.singletonList(response.getId())).exec();
        if (containers.isEmpty()) {
            throw new InvalidRequestException(String.format("Container %s was not found", response.getId()));
        }
        return dockerMapper.mapContainer(containers.get(0));
    }

    public void deleteContainer(String connectionToken, String containerId) throws MissingConnectionException {
        DockerClient client = findDockerClient(connectionToken);
        client.removeContainerCmd(containerId).exec();
    }
}
