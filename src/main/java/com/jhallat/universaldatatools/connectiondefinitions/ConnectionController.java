package com.jhallat.universaldatatools.connectiondefinitions;

import com.jhallat.universaldatatools.activeconnection.ActiveConnectionService;
import com.jhallat.universaldatatools.connectiondefinitions.entities.*;
import com.jhallat.universaldatatools.connectionlog.ConnectionLogService;
import com.jhallat.universaldatatools.exceptions.InvalidRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.util.*;

@RestController
@CrossOrigin(origins = "http://localhost:4201")
@RequestMapping("/data-sources")
@RequiredArgsConstructor
@Slf4j
public class ConnectionController {

    //TODO Simplify the logic by wrapping repositories in service classes
    private final ConnectionTypeService connectionTypeService;
    private final ConnectionRepository connectionRepository;
    private final ActiveConnectionService activeConnectionService;
    private final ConnectionTokenService connectionTokenService;
    private final ConnectionService connectionService;

    @GetMapping("/types")
    ResponseEntity<List<ConnectionType>> getTypes() {
        return ResponseEntity.ok(connectionTypeService.findAll());
    }

    @GetMapping("/connections")
    ResponseEntity<List<ConnectionDefinition>> getConnections() {
        return ResponseEntity.ok(connectionRepository.findAll());
    }

    @PostMapping("/connection")
    ResponseEntity<ConnectionDefinition> createConnection(
            @RequestBody @Valid ConnectionDefinition connectionDefinition) throws InvalidRequestException {
        return ResponseEntity.status(HttpStatus.CREATED).body(connectionService.createConnection(connectionDefinition));
    }

    @PutMapping("/disconnect/{token}")
    ResponseEntity<Void> diconnect(@PathVariable String token) {
        activeConnectionService.remove(token);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/connect/{id}")
    ConnectionToken connect(@PathVariable int id) {
        ConnectionToken connectionToken = connectionTokenService.createToken(id);
        //TODO Throw an error if unable to connect
        if (activeConnectionService.connect(connectionToken)) {
            return connectionToken;
        } else {
            return new InvalidConnectionToken("Unable to connect to data source");
        }
    }


}
