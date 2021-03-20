package com.jhallat.universaldatatools.connectiondefinitions;

import com.jhallat.universaldatatools.connectiondefinitions.entities.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ConnectionTokenService {

    private final ConnectionRepository connectionRepository;
    private final ConnectionTypeService connectionTypeService;
    private final ConnectionPropertyRepository connectionPropertyRepository;
    private final ConnectionPropertyValueRepository connectionPropertyValueRepository;

    /**
     * Creates a connection token for the provided id. If the provided id is not found in
     * the connection repository, an invalid token will be returned.
     * @param id Connection id
     * @return A connection token for the provided id
     */
    public ConnectionToken createToken(int id) {
        Optional<ConnectionDefinition> connectionDefinitionFound = connectionRepository.findById(id);
        if (connectionDefinitionFound.isEmpty()) {
            return new InvalidConnectionToken("Connection not found or expired");
        }
        ConnectionDefinition connectionDefinition = connectionDefinitionFound.get();
        Optional<ConnectionType> connectionTypeFound = connectionTypeService.findById(connectionDefinition.getTypeLabel());
        if (connectionTypeFound.isEmpty()) {
            return new InvalidConnectionToken("Connection type is not valid");
        }
        ConnectionType connectionType = connectionTypeFound.get();
        return new ConnectionToken(UUID.randomUUID().toString(),
                connectionType.getDescription(),
                connectionType.getLabel(),
                connectionType.getPage(),
                connectionDefinition.getDescription(),
                true,
                createPropertyMap(connectionDefinition.getId()));
    }

    private Map<String, String> createPropertyMap(int connectionId) {
        Map<String, String> propertyMap = new HashMap<>();

        List<ConnectionPropertyValue> connectionPropertyValues =
                connectionPropertyValueRepository.findByConnectionId(connectionId);
        for (ConnectionPropertyValue value : connectionPropertyValues) {
            Optional<ConnectionProperty> propertyFound = connectionPropertyRepository.findById(value.getPropertyId());
            //TODO Replace the property description with an enumeration
            propertyFound.ifPresent(connectionProperty -> propertyMap.put(connectionProperty.getDescription(), value.getValue()));
        }

        return propertyMap;
    }
}
