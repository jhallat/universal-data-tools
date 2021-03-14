package com.jhallat.universaldatatools.docker;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DockerConfiguration {

    @Bean(name="dockerConnectionFactory")
    public DockerConnectionFactory getDockerConnectionFactory() {
        return new DockerConnectionFactory();
    }

}
