package com.jhallat.universaldatatools.connectiondefinitions;

import com.jhallat.universaldatatools.activeconnection.ActiveConnectionService;
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
    private final ConnectionPropertyRepository connectionPropertyRepository;
    private final ConnectionPropertyValueRepository connectionPropertyValueRepository;
    private final ActiveConnectionService activeConnectionService;

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
                connectionTypeService.findById(connectionDefinition.getTypeId());
        if (connectionTypeFound.isEmpty()) {
            throw new InvalidRequestException(
                    String.format("Invalid connection type id [%s]", connectionDefinition.getTypeId() ));
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
                .forEach(value -> connectionPropertyValueRepository.save(value));

        return ResponseEntity.status(HttpStatus.CREATED).body(savedConnectionDefinition);
    }

    @GetMapping("/connect/{id}")
    ResponseEntity<ConnectionToken> connect(@PathVariable int id) throws InvalidRequestException {
        Optional<ConnectionDefinition> connectionDefinitionFound = connectionRepository.findById(id);
        if (connectionDefinitionFound.isEmpty()) {
            return ResponseEntity.ok(new ConnectionToken("",
                    ConnectionLabel.NONE, "", false, new HashMap<String, String>()));
        }
        ConnectionDefinition connectionDefinition = connectionDefinitionFound.get();
        Optional<ConnectionType> connectionTypeFound = connectionTypeService.findById(connectionDefinition.getTypeId());
        if (connectionTypeFound.isEmpty()) {
            throw new InvalidRequestException(
                    String.format("Invalid connection type id [%s]", connectionDefinition.getTypeId() ));
        }
        ConnectionToken connectionToken = new ConnectionToken(UUID.randomUUID().toString(),
                connectionTypeFound.get().getLabel(),
                connectionDefinition.getDescription(),
                true,
                createPropertyMap(connectionDefinition.getId()));
        //TODO Throw an error if unable to connect
        activeConnectionService.connect(connectionToken);
        return ResponseEntity.ok(connectionToken);
    }

    private Map<String, String> createPropertyMap(int connectionId) {
        Map<String, String> propertyMap = new HashMap<>();

        List<ConnectionPropertyValue> connectionPropertyValues =
                connectionPropertyValueRepository.findByConnectionId(connectionId);
        for (ConnectionPropertyValue value : connectionPropertyValues) {
            Optional<ConnectionProperty> propertyFound = connectionPropertyRepository.findById(value.getPropertyId());
            if (propertyFound.isPresent()) {
                //TODO Replace the property description with an enumeration
                propertyMap.put(propertyFound.get().getDescription(), value.getValue());
            }
        }

        return propertyMap;
    }
}
