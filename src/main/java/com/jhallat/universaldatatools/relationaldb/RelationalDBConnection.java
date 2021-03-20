package com.jhallat.universaldatatools.relationaldb;

import com.jhallat.universaldatatools.activeconnection.ActiveConnection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.SQLException;

@RequiredArgsConstructor
@Slf4j
public class RelationalDBConnection extends ActiveConnection {

    private final String label;
    private final Connection connection;

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public void close() {
        try {
            connection.close();
        } catch (SQLException sqlException) {
            log.warn("JDBC connection did not close properly");
        }
    }

    public Connection getConnection() { return connection; }

}
