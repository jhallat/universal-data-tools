package com.jhallat.universaldatatools.relationaldb;

import com.jhallat.universaldatatools.activeconnection.ActiveConnection;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.SQLException;

@Slf4j
public class RelationalDBConnection extends ActiveConnection {

    private final String label;
    private final HikariDataSource dataSource;

    public RelationalDBConnection(String label,
                                  String url,
                                  String username,
                                  String password) {
        this.label = label;
        HikariConfig config  = new HikariConfig();
        config.setJdbcUrl(url);
        config.setUsername(username);
        config.setPassword(password);
        dataSource = new HikariDataSource(config);
    }


    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public void close() {
        dataSource.close();
    }

    public Connection getConnection() throws SQLException { return dataSource.getConnection(); }

}
