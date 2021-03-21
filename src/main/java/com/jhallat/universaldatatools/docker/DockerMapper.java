package com.jhallat.universaldatatools.docker;

import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.SearchItem;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class DockerMapper {

    private static final DateTimeFormatter CREATED_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public ContainerDTO mapContainer(Container container) {
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
        return containerDTO;
    }

    public SearchItemDTO mapSearchItem(SearchItem searchItem) {
        SearchItemDTO searchItemDTO = new SearchItemDTO();
        searchItemDTO.setDescription(searchItem.getDescription());
        searchItemDTO.setName(searchItem.getName());
        searchItemDTO.setOfficial(searchItem.isOfficial());
        searchItemDTO.setStars(searchItem.getStarCount());
        return searchItemDTO;
    }
}
