package com.jhallat.universaldatatools.relationaldb;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RelationalDBConfiguration {

    @Bean(name="relationDBConnectionFactory")
    public RelationalDBConnectionFactory getRelationalDBConnectionFactory() {
        return new RelationalDBConnectionFactory();
    }

}
