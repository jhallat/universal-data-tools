package com.jhallat.universaldatatools.docker;

import com.jhallat.universaldatatools.exceptions.InvalidRequestException;
import com.jhallat.universaldatatools.exceptions.MissingConnectionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4201")
@RequestMapping("/docker")
@RequiredArgsConstructor
@Slf4j
public class DockerController {

    private final DockerService dockerService;

    @GetMapping("/containers")
    public ResponseEntity<List<ContainerDTO>> getDockerContainers(
            @RequestHeader("connection-token") String connectionToken) throws MissingConnectionException, InvalidRequestException {
        if (connectionToken == null) {
            throw new InvalidRequestException("Missing connection token");
        }
        List<ContainerDTO> containerDTOS = dockerService.getContainers(connectionToken);
        return ResponseEntity.ok(containerDTOS);
    }

    //TODO Maybe this should be a PUT?
    @PostMapping("/container/start/{containerId}")
    public ContainerDTO startDockerContainer(@RequestHeader("connection-token") String connectionToken,
                                             @PathVariable("containerId") String containerId) throws InvalidRequestException {
        if (connectionToken == null) {
            throw new InvalidRequestException("Missing connection token");
        }
        try {
            return dockerService.startContainer(connectionToken, containerId);
        } catch (MissingConnectionException exception) {
            throw new InvalidRequestException(exception.getMessage());
        }
    }

    //TODO Maybe this should be a PUT?
    @PostMapping("/container/stop/{containerId}")
    public ContainerDTO stopDockerContainer(@RequestHeader("connection-token") String connectionToken,
                                            @PathVariable("containerId") String containerId)
            throws InvalidRequestException, MissingConnectionException {
        if (connectionToken == null) {
            throw new InvalidRequestException("Missing connection token");
        }
        return dockerService.stopContainer(connectionToken, containerId);
    }

    @GetMapping("/images/search/{search}")
    public List<SearchItemDTO> findImages(@RequestHeader("connection-token") String connectionToken,
                                          @PathVariable String search,
                                          @DefaultValue("false") @QueryParam("official-only") Boolean officialOnly,
                                          @DefaultValue("0") @QueryParam("minimum-rating") Integer minimumRating)
            throws InvalidRequestException, MissingConnectionException {

        log.info("Search for images, search: {}, officialOnly: {}, minimumRating: {}", search, officialOnly, minimumRating);
        if (connectionToken == null) {
            throw new InvalidRequestException("Missing connection token");
        }
        List<SearchItemDTO> searchItemDTOS = dockerService.searchImages(connectionToken, search, officialOnly, minimumRating);
        log.info("{} items found.", searchItemDTOS.size());
        return searchItemDTOS;

    }

    @PostMapping("/container/create")
    public ContainerDTO createContainer(@RequestHeader("connection-token") String connectionToken,
                                        @RequestBody @Valid ContainerCreationDefinition definition)
            throws MissingConnectionException, InvalidRequestException {
        return dockerService.createContainer(connectionToken, definition);
    }

    @DeleteMapping("/container/{containerId}")
    public ResponseEntity<Void> deleteContainer(@RequestHeader("connection-token") String connectionToken,
                                                @PathVariable("containerId") String containerId)
            throws MissingConnectionException {
        dockerService.deleteContainer(connectionToken, containerId);
        return ResponseEntity.noContent().build();
    }
}
