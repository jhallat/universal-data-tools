package com.jhallat.universaldatatools.docker;

import com.jhallat.universaldatatools.connectiondefinitions.ConnectionTypeProvider;
import com.jhallat.universaldatatools.connectiondefinitions.entities.ConnectionType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class DockerConfiguration {

    public static final String LABEL_DOCKER = "DOCKER";

    @Bean(name="dockerConnectionFactory")
    public DockerConnectionFactory getDockerConnectionFactory() {
        return new DockerConnectionFactory();
    }

    @Bean(name="dockerConnectionDefinitionProvider")
    public ConnectionTypeProvider getDefinitionProvider() {
        return new ConnectionTypeProvider() {

            @Override
            public List<ConnectionType> getTypes() {
                ConnectionType type = new ConnectionType();
                type.setDescription("Docker");
                //TODO replace with actual factory
                type.setFactory("dockerConnectionFactory");
                type.setLabel(LABEL_DOCKER);
                type.setPage("docker");
                return new ArrayList<ConnectionType>() {
                    {
                        add(type);
                    }
                };
            }
        };
    }

}
