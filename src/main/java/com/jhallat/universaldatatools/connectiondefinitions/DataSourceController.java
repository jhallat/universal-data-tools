package com.jhallat.universaldatatools.connectiondefinitions;

import com.jhallat.universaldatatools.activeconnection.ActiveConnectionService;
import com.jhallat.universaldatatools.connectiondefinitions.entities.*;
import com.jhallat.universaldatatools.exceptions.InvalidRequestException;
import lombok.RequiredArgsConstructor;
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
public class DataSourceController {

    //TODO Simplify the logic by wrapping repositories in service classes
    private final ConnectionTypeService connectionTypeService;
    private final ConnectionRepository connectionRepository;
    private final ConnectionPropertyValueRepository connectionPropertyValueRepository;
    private final ActiveConnectionService activeConnectionService;
    private final ConnectionTokenService connectionTokenService;

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
        Optional<ConnectionType> connectionTypeFound =
                connectionTypeService.findById(connectionDefinition.getTypeLabel());
        if (connectionTypeFound.isEmpty()) {
            throw new InvalidRequestException(
                    String.format("Invalid connection type label [%s]", connectionDefinition.getTypeLabel() ));
        }
        if (StringUtils.isBlank(connectionDefinition.getDescription())) {
            String connectionType = connectionTypeFound.get().getDescription();
            List<ConnectionDefinition> current = connectionRepository.findByDescriptionStartingWith(connectionType);
            boolean found = false;
            if (!current.stream().anyMatch((item) -> item.getDescription().equalsIgnoreCase(connectionType))) {
                found = true;
                connectionDefinition.setDescription(connectionType);
            }
            int index = 1;
            while (!found) {
                String description = connectionType + "-" + index;
                if (!current.stream().anyMatch((item) -> item.getDescription().equalsIgnoreCase(connectionType))) {
                    found = true;
                    connectionDefinition.setDescription(description);
                }
                index++;
            }
        }
        ConnectionDefinition savedConnectionDefinition = connectionRepository.save(connectionDefinition);
        connectionDefinition.getProperties().stream().map(propertyValue ->
                new ConnectionPropertyValue(savedConnectionDefinition.getId(),
                        propertyValue.getPropertyId(),
                        propertyValue.getValue()))
                .forEach(connectionPropertyValueRepository::save);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedConnectionDefinition);
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
