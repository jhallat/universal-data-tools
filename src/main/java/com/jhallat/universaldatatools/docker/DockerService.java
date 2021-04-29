package com.jhallat.universaldatatools.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.PullImageResultCallback;
import com.github.dockerjava.api.model.*;
import com.jhallat.universaldatatools.activeconnection.ActiveConnection;
import com.jhallat.universaldatatools.activeconnection.ActiveConnectionService;
import com.jhallat.universaldatatools.connectionlog.ConnectionLogService;
import com.jhallat.universaldatatools.exceptions.InternalSystemException;
import com.jhallat.universaldatatools.exceptions.InvalidRequestException;
import com.jhallat.universaldatatools.exceptions.MissingConnectionException;
import com.jhallat.universaldatatools.status.StatusMessageController;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.apache.logging.log4j.util.Strings.isBlank;

@Service
@RequiredArgsConstructor
@Slf4j
public class DockerService {

    private final ActiveConnectionService activeConnectionService;
    private final DockerMapper dockerMapper;
    private final ConnectionLogService connectionLogService;
    private final StatusMessageController statusMessageController;

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
        try {
            client.startContainerCmd(containerId).exec();
        } catch (Exception exception) {
            connectionLogService.error("Docker", exception.getMessage());
        }
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
        try {
            client.stopContainerCmd(containerId).exec();
        } catch (Exception exception) {
            connectionLogService.error("Docker", exception.getMessage());
        }
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
        try {
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
        } catch (Exception exception) {
            connectionLogService.error("Docker", exception.getMessage());
        }
        return Collections.emptyList();
    }

    public ContainerDTO createContainer(String connectionToken, ContainerCreationDefinition definition)
            throws MissingConnectionException, InvalidRequestException {
        DockerClient client = findDockerClient(connectionToken);
        var command = client.createContainerCmd(definition.getImage());
        if (!StringUtils.isBlank(definition.getName())) {
            command.withName(definition.getName());
        }
        var hostConfig = HostConfig.newHostConfig();
        var portBindings = definition.getPublishedPorts().stream()
                .filter(port -> !StringUtils.isBlank(port.getMapping()))
                .map(port -> PortBinding.parse(port.getMapping()))
                .collect(Collectors.toList());
        if (!portBindings.isEmpty()) {
            hostConfig.withPortBindings(portBindings);
        }
        var volumes = definition.getVolumes().stream()
                .filter(volume -> !StringUtils.isBlank(volume.getMapping()))
                .map(volume -> Bind.parse(volume.getMapping()))
                .collect(Collectors.toList());
        if (!volumes.isEmpty()) {
            hostConfig.withBinds(volumes);
        }
        command.withHostConfig(hostConfig);
        var environmentVariables = definition.getEnvironmentVariables().stream()
                .filter(variable -> !isBlank(variable.getName()) && !isBlank(variable.getValue()))
                .map(variable -> String.format("%s=%s", variable.getName(), variable.getValue()))
                .peek(variable -> log.info("Environment Variable: {}", variable))
                .collect(Collectors.toList());
        command.withEnv(environmentVariables);

        try {
            var response = command.exec();
            List<Container> containers =
                    client.listContainersCmd().withIdFilter(Collections.singletonList(response.getId())).exec();
            if (containers.isEmpty()) {
                throw new InvalidRequestException(String.format("Container %s was not found", response.getId()));
            }
            return dockerMapper.mapContainer(containers.get(0));
        } catch (Exception exception) {
            log.error("Error creating container", exception);
            connectionLogService.error("Docker", exception.getMessage());
        }
        return null;
    }

    public void deleteContainer(String connectionToken, String containerId) throws MissingConnectionException {
        DockerClient client = findDockerClient(connectionToken);
        try {
            client.removeContainerCmd(containerId).exec();
        } catch (Exception exception) {
            log.error("Error deleting container", exception);
            connectionLogService.error("Docker", exception.getMessage());
        }
    }

    public List<ImageDTO> getImages(String connectionToken) throws MissingConnectionException, InternalSystemException {
        DockerClient client = findDockerClient(connectionToken);
        try {
            List<Image> images = client.listImagesCmd().exec();
            return images.stream().map(image -> new ImageDTO(image.getId(),
                    Arrays.stream(image.getRepoTags()).collect(Collectors.joining(","))))
                    .toList();
        } catch (Exception exception) {
            log.error("Error getting image list", exception);
            connectionLogService.error("Docker", exception.getMessage());
            throw new InternalSystemException(exception);
        }
    }

    public void pullImage(String connectionToken, String image, String tag) throws MissingConnectionException {
        DockerClient client = findDockerClient(connectionToken);

        String imageTag = String.format("%s:%s", image, tag);
        client.pullImageCmd(imageTag).exec(new PullImageResultCallback() {
            @Override
            public void onNext(PullResponseItem item) {
                super.onNext(item);
                if (item.isPullSuccessIndicated()) {
                    log.info(String.format("%s successfully pulled", imageTag));
                    connectionLogService.info("%s successfully pulled", imageTag);
                    statusMessageController.sendMessage("Docker", String.format("%s successfully pulled", imageTag));
                }
                if (item.isErrorIndicated()) {
                    log.error(String.format("Error occurred pulling %s", imageTag));
                    connectionLogService.info("Error occurred pulling %s", imageTag);
                    statusMessageController.sendMessage("Docker", String.format("Error occurred pulling %s", imageTag));
                }
            }
        });
    }

    public List<String> getTags(String image) {
        RestTemplate restTemplate = new RestTemplate();
        try {
            String dockerTagUrl = String.format("https://registry.hub.docker.com/v1/repositories/%s/tags", image);
            ResponseEntity<Tag[]> response = restTemplate.getForEntity(dockerTagUrl, Tag[].class);
            return Arrays.asList(response.getBody()).stream().map(tag -> tag.name()).toList();
        } catch(HttpClientErrorException exception) {
            return Collections.emptyList();
        }
    }

}
