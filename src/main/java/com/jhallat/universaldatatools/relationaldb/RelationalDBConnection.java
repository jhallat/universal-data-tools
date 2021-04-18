package com.jhallat.universaldatatools.relationaldb;

import com.jhallat.universaldatatools.activeconnection.ActiveConnection;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class RelationalDBConnection extends ActiveConnection {

    private final String label;
    private final String url;
    private final String username;
    private final String password;
    private Map<String, HikariDataSource> tableMap = new HashMap<>();

    public RelationalDBConnection(String label,
                                  String url,
                                  String username,
                                  String password) {
        this.label = label;
        this.url = url;
        this.username = username;
        this.password = password;
        HikariConfig config  = new HikariConfig();
        config.setJdbcUrl(url);
        config.setUsername(username);
        config.setPassword(password);
        tableMap.put("*", new HikariDataSource(config));
    }


    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public void close() {

        for (HikariDataSource dataSource : tableMap.values()) {
            dataSource.close();
        }
    }

    public Connection getConnection() throws SQLException { return tableMap.get("*").getConnection(); }

    public Connection getConnection(String dbName) throws SQLException {
        if (!tableMap.containsKey(dbName)) {
            HikariConfig config  = new HikariConfig();
            log.info("Connection to {}", String.format("%s/%s",url, dbName ));
            config.setJdbcUrl(String.format("%s%s",url, dbName ));
            config.setUsername(username);
            config.setPassword(password);
            tableMap.put(dbName, new HikariDataSource(config));
        }
        return tableMap.get(dbName).getConnection();
    }
}
