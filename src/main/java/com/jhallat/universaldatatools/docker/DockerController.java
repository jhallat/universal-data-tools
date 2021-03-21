package com.jhallat.universaldatatools.docker;

import com.jhallat.universaldatatools.exceptions.InvalidRequestException;
import com.jhallat.universaldatatools.exceptions.MissingConnectionException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4201")
@RequestMapping("/docker")
@RequiredArgsConstructor
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
                                             @PathVariable("containerId") String containerId) throws InvalidRequestException {
        if (connectionToken == null) {
            throw new InvalidRequestException("Missing connection token");
        }
        try {
            return dockerService.stopContainer(connectionToken, containerId);
        } catch (MissingConnectionException exception) {
            throw new InvalidRequestException(exception.getMessage());
        }
    }

}
