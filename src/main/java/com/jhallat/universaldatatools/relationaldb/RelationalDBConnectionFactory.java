package com.jhallat.universaldatatools.relationaldb;

import com.jhallat.universaldatatools.activeconnection.ActiveConnection;
import com.jhallat.universaldatatools.activeconnection.ActiveConnectionFactory;
import com.jhallat.universaldatatools.activeconnection.NullConnection;
import com.jhallat.universaldatatools.connectiondefinitions.entities.ConnectionToken;
import com.jhallat.universaldatatools.connectionlog.ConnectionLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Slf4j
@RequiredArgsConstructor
public class RelationalDBConnectionFactory implements ActiveConnectionFactory {

    private final ConnectionLogService connectionLogService;

    @Override
    public ActiveConnection createConnection(ConnectionToken connectionToken) {

        if (StringUtils.equalsIgnoreCase(connectionToken.getLabel(), RelationalDBConfiguration.LABEL_POSTGRESQL)) {
            String url = connectionToken.getProperty(RelationalDBConfiguration.PROPERTY_URL);
            String username = connectionToken.getProperty(RelationalDBConfiguration.PROPERTY_USERNAME);
            String password = connectionToken.getProperty(RelationalDBConfiguration.PROPERTY_PASSWORD);
            String connectionString = String.format("jdbc:postgresql://%s/", url);
            return new RelationalDBConnection(RelationalDBConfiguration.LABEL_POSTGRESQL,
                        connectionString,
                        username,
                        password);
        } else {
            connectionLogService.error("Invalid relational database type %s", connectionToken.getLabel());
            log.error("Invalid relational database type {}", connectionToken.getLabel());
            return new NullConnection();
        }

    }
}
