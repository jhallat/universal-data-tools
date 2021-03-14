package com.jhallat.universaldatatools.docker;

import com.jhallat.universaldatatools.exceptions.InvalidRequestException;
import com.jhallat.universaldatatools.exceptions.MissingConnectionException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/docker")
@RequiredArgsConstructor
public class DockerController {

    private final DockerService dockerService;

    @GetMapping("/containers")
    public ResponseEntity<List<ContainerDTO>> getDockerContainers(
            @RequestHeader("connection-token") String connectionToken) throws InvalidRequestException {
        if (connectionToken == null) {
            throw new InvalidRequestException("Missing connection token");
        }
        try {
            List<ContainerDTO> containerDTOS = dockerService.getContainers(connectionToken);
            return ResponseEntity.ok(containerDTOS);
        } catch (MissingConnectionException exception) {
            throw new InvalidRequestException(exception.getMessage());
        }
    }

}
