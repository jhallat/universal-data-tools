package com.jhallat.universaldatatools.connectiondefinitions;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConnectionTypeService {

    private final ConnectionTypeRepository connectionTypeRepository;
    private final ConnectionPropertyRepository connectionPropertyRepository;
    private final ConnectionTypePropertyRepository connectionTypePropertyRepository;

    private Map<Integer, ConnectionType> connectionTypeCache;

    private void loadCache() {
        connectionTypeCache = new HashMap<>();
        List<ConnectionType> connectionTypes = connectionTypeRepository.findAll();
        List<ConnectionProperty> connectionProperties = connectionPropertyRepository.findAll();
        List<ConnectionTypeProperty> connectionTypeProperties = connectionTypePropertyRepository.findAll();
        Map<Integer, ConnectionProperty> connectionPropertyMap =
                connectionProperties.stream()
                .collect(Collectors.toMap(ConnectionProperty::getId, connProperty -> connProperty));
        Map<Integer, List<ConnectionTypeProperty>> connectionTypePropertyMap =
                connectionTypeProperties.stream()
                .collect(Collectors.groupingBy(ConnectionTypeProperty::getTypeId));
        for (ConnectionType connectionType : connectionTypes) {
            List<ConnectionTypeProperty> typeProperties = connectionTypePropertyMap.get(connectionType.getId());
            for (ConnectionTypeProperty typeProperty : typeProperties) {
                ConnectionProperty property = connectionPropertyMap.get(typeProperty.getPropertyId());
                connectionType.addPropertyDefinition(
                        new PropertyDefinition(property.getId(),
                                property.getDescription(),
                                typeProperty.getRequired() > 0,
                                 property.getMasked() > 0));
            }
            connectionTypeCache.put(connectionType.getId(), connectionType);
        }
    }

    public List<ConnectionType> findAll() {
        if (connectionTypeCache == null) {
            loadCache();
        }
        List<ConnectionType> connectionTypes = new ArrayList(connectionTypeCache.values());
        connectionTypes.sort(Comparator.comparing(ConnectionType::getDescription));
        return connectionTypes;
    }

    public Optional<ConnectionType> findById(int id) {
        if (connectionTypeCache == null) {
            loadCache();
        }
        if (connectionTypeCache.containsKey(id)) {
            return Optional.of(connectionTypeCache.get(id));
        }
        return Optional.empty();
    }
}
