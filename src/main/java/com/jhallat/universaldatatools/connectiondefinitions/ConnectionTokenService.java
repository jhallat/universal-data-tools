package com.jhallat.universaldatatools.connectiondefinitions;

import com.jhallat.universaldatatools.connectiondefinitions.entities.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConnectionTokenService {

    //TODO Create a connection service that links the properties to connection of use a join on entity
    private final ConnectionRepository connectionRepository;
    private final ConnectionTypeService connectionTypeService;
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
        List<ConnectionPropertyValue> propertyValues = connectionPropertyValueRepository.findByConnectionId(id);
        return new ConnectionToken(UUID.randomUUID().toString(),
                connectionType.getDescription(),
                connectionType.getLabel(),
                connectionType.getPage(),
                connectionDefinition.getDescription(),
                true,
                propertyValues.stream()
                        .collect(Collectors.toMap(ConnectionPropertyValue::getPropertyId, ConnectionPropertyValue::getValue)));
    }


}
