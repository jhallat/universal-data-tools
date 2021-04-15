package com.jhallat.universaldatatools.relationaldb;

import com.jhallat.universaldatatools.connectiondefinitions.ConnectionTypeProvider;
import com.jhallat.universaldatatools.connectiondefinitions.entities.ConnectionType;
import com.jhallat.universaldatatools.connectiondefinitions.entities.PropertyDefinition;
import com.jhallat.universaldatatools.connectionlog.ConnectionLogService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class RelationalDBConfiguration {

    public static final String LABEL_POSTGRESQL = "POSTGRESQL";

    public static final int PROPERTY_URL = 1;
    public static final int PROPERTY_USERNAME = 2;
    public static final int PROPERTY_PASSWORD = 3;

    @Bean(name="relationalDBConnectionFactory")
    public RelationalDBConnectionFactory getRelationalDBConnectionFactory(ConnectionLogService connectionLogService) {
        return new RelationalDBConnectionFactory(connectionLogService);
    }

    @Bean(name="relationalDBConnectionDefinitionProvider")
    public ConnectionTypeProvider getDefinitionProvider() {
        return new ConnectionTypeProvider() {

            @Override
            public List<ConnectionType> getTypes() {
                ConnectionType type = new ConnectionType();
                type.setDescription("PostgreSQL");
                //TODO replace with actual factory
                type.setFactory("relationalDBConnectionFactory");
                type.setLabel(LABEL_POSTGRESQL);
                type.setPage("database");
                type.addPropertyDefinition(new PropertyDefinition(PROPERTY_URL,"URL", true, false));
                type.addPropertyDefinition(new PropertyDefinition(PROPERTY_USERNAME,"Username", true, false));
                type.addPropertyDefinition(new PropertyDefinition(PROPERTY_PASSWORD,"Password", true, true));
                return new ArrayList<>() {
                    {
                        add(type);
                    }
                };
            }
        };
    }
}
