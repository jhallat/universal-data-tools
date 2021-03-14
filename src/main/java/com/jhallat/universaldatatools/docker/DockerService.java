package com.jhallat.universaldatatools.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Container;
import com.jhallat.universaldatatools.activeconnection.ActiveConnection;
import com.jhallat.universaldatatools.activeconnection.ActiveConnectionService;
import com.jhallat.universaldatatools.exceptions.MissingConnectionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DockerService {

    private static final DateTimeFormatter CREATED_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final ActiveConnectionService activeConnectionService;

    public List<ContainerDTO> getContainers(String connectionToken) throws MissingConnectionException {
        List<ContainerDTO> containerDTOS = new ArrayList<>();
        ActiveConnection activeConnection = activeConnectionService.getConnection(connectionToken);
        if  (activeConnection instanceof DockerConnection connection) {
            DockerClient client = connection.getDockerClient();
            List<Container> containers = client.listContainersCmd().withShowAll(true).exec();
            for (Container container : containers) {
                ContainerDTO containerDTO = new ContainerDTO();
                containerDTO.setContainerId(container.getId());
                containerDTO.setImage(container.getImage());
                containerDTO.setCommand(container.getCommand());
                LocalDateTime createdTime = Instant.ofEpochMilli(container.getCreated() * 1000).atZone(ZoneId.systemDefault()).toLocalDateTime();
                containerDTO.setCreated(createdTime.format(CREATED_TIME_FORMAT));
                containerDTO.setStatus(container.getStatus());
                List<String> ports = Arrays.stream(container.getPorts())
                        .map(port -> port.getPrivatePort() + " -> " + port.getPublicPort()).collect(Collectors.toList());
                containerDTO.setPorts(StringUtils.join(ports, ", "));
                containerDTO.setNames(StringUtils.join(container.getNames(), ", "));
                containerDTOS.add(containerDTO);
            }
        } else {
            log.warn("Expected DOCKER connection, found {}", activeConnection.getActiveConnectionType().name());
            throw new MissingConnectionException("Connection missing or expired.");
        }
        return containerDTOS;
    }

}
