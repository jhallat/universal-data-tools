package com.jhallat.universaldatatools.relationaldb;

import com.jhallat.universaldatatools.activeconnection.ActiveConnection;
import com.jhallat.universaldatatools.activeconnection.ActiveConnectionFactory;
import com.jhallat.universaldatatools.activeconnection.NullConnection;
import com.jhallat.universaldatatools.connectiondefinitions.entities.ConnectionToken;
import com.jhallat.universaldatatools.connectionlog.ConnectionLogService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Component
@RequiredArgsConstructor
public class RelationalDBConnectionFactory implements ActiveConnectionFactory {

    private ConnectionLogService connectionLogService;

    @Override
    public ActiveConnection createConnection(ConnectionToken connectionToken) {

        if (StringUtils.equalsIgnoreCase(connectionToken.getLabel(), RelationalDBConfiguration.LABEL_POSTGRESQL)) {
            String url = connectionToken.getProperty(RelationalDBConfiguration.PROPERTY_URL);
            String username = connectionToken.getProperty(RelationalDBConfiguration.PROPERTY_USERNAME);
            String password = connectionToken.getProperty(RelationalDBConfiguration.PROPERTY_PASSWORD);
            String connectionString = String.format("jdbc:postgresql://%s", url);
            try {
                Connection connection = DriverManager.getConnection(connectionString, username, password);
                return new RelationalDBConnection(RelationalDBConfiguration.LABEL_POSTGRESQL, connection);
            } catch (SQLException exception) {
                connectionLogService.error("Postgresql", exception.getMessage());
                return new NullConnection();
            }
        } else {
            return new NullConnection();
        }

    }
}
