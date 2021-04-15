package com.jhallat.universaldatatools.connectiondefinitions;

import com.jhallat.universaldatatools.connectiondefinitions.entities.ConnectionDefinition;
import com.jhallat.universaldatatools.connectiondefinitions.entities.ConnectionPropertyValue;
import com.jhallat.universaldatatools.connectiondefinitions.entities.ConnectionType;
import com.jhallat.universaldatatools.connectionlog.ConnectionLog;
import com.jhallat.universaldatatools.connectionlog.ConnectionLogService;
import com.jhallat.universaldatatools.exceptions.InvalidRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ConnectionService {

    private final ConnectionTypeService connectionTypeService;
    private final ConnectionLogService connectionLogService;
    private final ConnectionRepository connectionRepository;
    private final ConnectionPropertyValueRepository connectionPropertyValueRepository;

    public ConnectionDefinition createConnection(ConnectionDefinition connectionDefinition) throws InvalidRequestException {
        log.info("Create Connection: {}", connectionDefinition);
        ConnectionType connectionType =
                connectionTypeService.findById(connectionDefinition.getTypeLabel()).orElseThrow(() -> {
                            connectionLogService.error(
                                    "Connection","Invalid connection type label [%s]",
                                    connectionDefinition.getTypeLabel());
                            return new InvalidRequestException(
                                    String.format("Invalid connection type label [%s]", connectionDefinition.getTypeLabel() ));
                        }
                );
        String description = generateDescription(connectionDefinition, connectionType);
        //TODO should probably clone instead of altering parameter
        connectionDefinition.setDescription(description);
        ConnectionDefinition savedConnectionDefinition = connectionRepository.save(connectionDefinition);

        connectionDefinition.getProperties().stream().map(propertyValue ->
                new ConnectionPropertyValue(savedConnectionDefinition.getId(),
                        propertyValue.getPropertyId(),
                        propertyValue.getValue()))
                .forEach(connectionPropertyValueRepository::save);
        connectionLogService.info(connectionDefinition.getTypeLabel(), "Connected.");
        return savedConnectionDefinition;
    }

    private String generateDescription(ConnectionDefinition connectionDefinition, ConnectionType connectionType) {
        String potentialName =
            StringUtils.defaultIfBlank(connectionDefinition.getDescription(), connectionType.getDescription());

        List<ConnectionDefinition> current = connectionRepository.findByDescriptionStartingWith(potentialName);
            if (!current.stream().anyMatch((item) -> item.getDescription().equalsIgnoreCase(potentialName))) {
                return potentialName;
            }
            int index = 1;
            while (true) {
                String description = connectionType + "-" + index;
                if (!current.stream().anyMatch((item) -> item.getDescription().equalsIgnoreCase(potentialName))) {
                    return description;
                }
                index++;
            }

    }
}
