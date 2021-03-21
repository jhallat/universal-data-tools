package com.jhallat.universaldatatools.activeconnection;

import com.jhallat.universaldatatools.connectiondefinitions.ConnectionTypeService;
import com.jhallat.universaldatatools.connectiondefinitions.entities.ConnectionToken;
import com.jhallat.universaldatatools.connectiondefinitions.entities.ConnectionType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActiveConnectionService {

    private final ConnectionTypeService connectionTypeService;
    private final ApplicationContext applicationContext;

    private final Map<String, ActiveConnectionFactory> factoryMap = new HashMap<>();
    private final Map<String, ActiveConnection> activeConnectionMap = new HashMap<>();

    private void initializeFactoryMap() {
        List<ConnectionType> connectionTypes = connectionTypeService.findAll();
        for (ConnectionType connectionType : connectionTypes) {
            if (applicationContext.containsBeanDefinition(connectionType.getFactory())) {
                ActiveConnectionFactory factory = applicationContext.getBean(connectionType.getFactory(),
                        ActiveConnectionFactory.class);
                factoryMap.put(connectionType.getLabel(), factory);
            } else {
                log.warn("Missing connection factory '{}'", connectionType.getFactory());
            }
        }
    }

    //TODO Should this method create the connection token?
    public boolean connect(ConnectionToken token) {
        if (factoryMap.isEmpty()) {
            initializeFactoryMap();
        }
        ActiveConnectionFactory factory = factoryMap.get(token.getLabel());
        if (factory == null) {
            return false;
        }
        ActiveConnection connection = factory.createConnection(token);
        log.info("Adding connection {} for token {} to active connection map", connection.getLabel(), token.getToken());
        activeConnectionMap.put(token.getToken(), connection);
        return true;
    }

    public ActiveConnection getConnection(String connectionToken) {
        return activeConnectionMap.getOrDefault(connectionToken, new NullConnection());
    }

    public List<ActiveConnectionDTO> findAll() {

        List<ActiveConnectionDTO> activeConnectionDTOS = new ArrayList<>();
        for (Map.Entry<String, ActiveConnection> entry : activeConnectionMap.entrySet()) {
            String token = entry.getKey();
            String label = entry.getValue().getLabel();
            LocalDateTime lastUsed = Instant.ofEpochMilli(
                    entry.getValue().getLastTimeUsed()).atZone(ZoneId.systemDefault()).toLocalDateTime();
            activeConnectionDTOS.add(new ActiveConnectionDTO(token, label, lastUsed));
        }
        return Collections.unmodifiableList(activeConnectionDTOS);
    }

    public void removeAll() {
        List<String> tokens = List.copyOf(activeConnectionMap.keySet());
        for (String token : tokens) {
            ActiveConnection connection = activeConnectionMap.remove(token);
            connection.close();
        }
    }

    public void remove(String token) {
        log.info("Removing token {}", token);
        ActiveConnection connection = activeConnectionMap.remove(token);
        connection.close();
    }
}
