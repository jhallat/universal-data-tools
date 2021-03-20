package com.jhallat.universaldatatools.connectiondefinitions;

import com.jhallat.universaldatatools.connectiondefinitions.entities.ConnectionType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ConnectionTypeService {

    private final ApplicationContext applicationContext;

    private Map<String, ConnectionType> connectionTypeCache;

    private void loadCache() {
        connectionTypeCache = new HashMap<>();
        String[] beanNames = applicationContext.getBeanNamesForType(ConnectionTypeProvider.class);
        for (String beanName : beanNames) {
            ConnectionTypeProvider provider = applicationContext.getBean(beanName, ConnectionTypeProvider.class);
            for (ConnectionType connection : provider.getTypes()) {
                connectionTypeCache.put(connection.getLabel(), connection);
            }
        }
    }

    public List<ConnectionType> findAll() {
        if (connectionTypeCache == null) {
            loadCache();
        }
        List<ConnectionType> connectionTypes = new ArrayList<>(connectionTypeCache.values());
        connectionTypes.sort(Comparator.comparing(ConnectionType::getDescription));
        return connectionTypes;
    }

    public Optional<ConnectionType> findById(String typeLabel) {
        if (connectionTypeCache == null) {
            loadCache();
        }
        if (connectionTypeCache.containsKey(typeLabel)) {
            return Optional.of(connectionTypeCache.get(typeLabel));
        }
        return Optional.empty();
    }
}
