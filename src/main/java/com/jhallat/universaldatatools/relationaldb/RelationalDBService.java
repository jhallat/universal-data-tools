package com.jhallat.universaldatatools.relationaldb;

import com.jhallat.universaldatatools.activeconnection.ActiveConnection;
import com.jhallat.universaldatatools.activeconnection.ActiveConnectionService;
import com.jhallat.universaldatatools.connectionlog.ConnectionLogService;
import com.jhallat.universaldatatools.exceptions.MissingConnectionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jvnet.hk2.annotations.Service;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class RelationalDBService {

    private final ActiveConnectionService activeConnectionService;
    private final ConnectionLogService connectionLogService;

    private ConnectionDef findConnection(String connectionToken) throws MissingConnectionException, SQLException {
        ActiveConnection activeConnection = activeConnectionService.getConnection(connectionToken);
        if (activeConnection instanceof RelationalDBConnection connection) {
            return new ConnectionDef(connection.getLabel(), connection.getConnection());
        }
        log.warn("Expected Relational DB connection, found {} for token {}", activeConnection.getLabel(), connectionToken);
        throw new MissingConnectionException();
    }

    public List<DatabaseDef> getDatabases(String connectionToken) throws MissingConnectionException, SQLException {

        List<DatabaseDef> databases = new ArrayList<>();
        Set<String> dbNames = new HashSet<>();
        Map<String, List<TableDef>> tableMap = new HashMap<>();
        Map<String, List<ViewDef>> viewMap = new HashMap<>();
        String dbsql = """
                    SELECT table_catalog, table_type, table_name FROM pg_database db
                    INNER JOIN information_schema.tables tbl
                       on tbl.table_catalog = db.datname
                    WHERE db.datistemplate = false
                    """;
        ConnectionDef connectionDef = findConnection(connectionToken);
        try (Connection connection = connectionDef.connection();
            ResultSet results = connection.prepareStatement(dbsql).executeQuery()) {
            while (results.next()) {
                String database = results.getString("table_catalog");
                String tableType = results.getString("table_type");
                String tableName = results.getString("table_name");
                dbNames.add(database);
                switch (tableType) {
                    case "BASE TABLE":
                        if (!tableMap.containsKey(database)) {
                            tableMap.put(database, new ArrayList<>());
                        }
                        tableMap.get(database).add(new TableDef(tableName));
                        break;
                    case "VIEW":
                        if (!viewMap.containsKey(database)) {
                            viewMap.put(database, new ArrayList<>());
                        }
                        viewMap.get(database).add(new ViewDef(tableName));
                        break;
                    default:
                        log.warn("Unexpected type {}", tableType);
                }
                for (String dbName : dbNames) {
                    databases.add(new DatabaseDef(dbName,
                            tableMap.getOrDefault(dbName, new ArrayList<>()),
                            viewMap.getOrDefault(dbName, new ArrayList<>())));
                }
            }
        }
        return Collections.unmodifiableList(databases);
    }
}
