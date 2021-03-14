package com.jhallat.universaldatatools.activeconnection;

import com.jhallat.universaldatatools.connectiondefinitions.ConnectionLabel;
import com.jhallat.universaldatatools.connectiondefinitions.ConnectionToken;
import com.jhallat.universaldatatools.connectiondefinitions.ConnectionType;
import com.jhallat.universaldatatools.connectiondefinitions.ConnectionTypeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActiveConnectionService {

    private final ConnectionTypeRepository connectionTypeRepository;
    private final ApplicationContext applicationContext;

    private Map<ConnectionLabel, ActiveConnectionFactory> factoryMap = new HashMap<>();
    private Map<String, ActiveConnection> activeConnectionMap = new HashMap<>();

    private void initializeFactoryMap() {
        List<ConnectionType> connectionTypes = connectionTypeRepository.findAll();
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
    public void connect(ConnectionToken token) {
        if (factoryMap.isEmpty()) {
            initializeFactoryMap();
        }
        //TODO handle missing factory
        ActiveConnectionFactory factory = factoryMap.get(token.getLabel());
        ActiveConnection connection = factory.createConnection(token);
        log.info("Adding token {} to active connection map", token.getToken());
        activeConnectionMap.put(token.getToken(), connection);
    }

    public ActiveConnection getConnection(String connectionToken) {
        return activeConnectionMap.getOrDefault(connectionToken, new NullConnection());
    }

}
