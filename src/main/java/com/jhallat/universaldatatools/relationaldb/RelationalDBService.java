package com.jhallat.universaldatatools.relationaldb;

import com.jhallat.universaldatatools.activeconnection.ActiveConnection;
import com.jhallat.universaldatatools.activeconnection.ActiveConnectionService;
import com.jhallat.universaldatatools.connectionlog.ConnectionLogService;
import com.jhallat.universaldatatools.exceptions.MissingConnectionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

// TODO Connection should be closed when finished, determine the cause of connection closed error
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

    private String createDataSQL(String schema, String table, List<ColumnDef> columns) {
       String selections = columns.stream().map(ColumnDef::name).collect(Collectors.joining(","));
       String sql = String.format("SELECT %s FROM %s.%s", selections, schema, table);
       // TODO Change to debug
       log.info(sql);
       return sql;
    }

    public List<DatabaseDef> getDatabases(String connectionToken) throws MissingConnectionException, SQLException {

        List<DatabaseDef> databases = new ArrayList<>();
        Set<String> dbNames = new HashSet<>();
        Map<String, List<TableDescription>> tableMap = new HashMap<>();
        Map<String, List<ViewDescription>> viewMap = new HashMap<>();
        String dbsql = """
                    SELECT table_catalog, table_type, table_name, table_schema FROM pg_database db
                    INNER JOIN information_schema.tables tbl
                       on tbl.table_catalog = db.datname
                    WHERE db.datistemplate = false
                    """;
        ConnectionDef connectionDef = findConnection(connectionToken);
        Connection connection = connectionDef.connection();
        try (ResultSet results = connection.prepareStatement(dbsql).executeQuery()) {
            while (results.next()) {
                String database = results.getString("table_catalog");
                String tableType = results.getString("table_type");
                String tableName = results.getString("table_name");
                String tableSchema = results.getString("table_schema");
                dbNames.add(database);
                switch (tableType) {
                    case "BASE TABLE" -> {
                        if (!tableMap.containsKey(database)) {
                            tableMap.put(database, new ArrayList<>());
                        }
                        tableMap.get(database).add(new TableDescription(tableName, tableSchema));
                    }
                    case "VIEW" -> {
                        if (!viewMap.containsKey(database)) {
                            viewMap.put(database, new ArrayList<>());
                        }
                        viewMap.get(database).add(new ViewDescription(tableName, tableSchema));
                    }
                    default -> log.warn("Unexpected type {}", tableType);
                }
            }
            for (String dbName : dbNames) {
                databases.add(new DatabaseDef(dbName,
                        tableMap.getOrDefault(dbName, new ArrayList<>()),
                        viewMap.getOrDefault(dbName, new ArrayList<>())));
            }
        }
        return Collections.unmodifiableList(databases);
    }

    public TableDef getTable(String connectionToken, String schema, String table) throws SQLException, MissingConnectionException {

        List<ColumnDef> columns = new ArrayList<>();
        List<List<String>> rows = new ArrayList<>();
        String tablesql = """
                SELECT column_name,
                       column_default,
                       is_nullable,
                       data_type,
                       character_maximum_length,
                       numeric_precision,
                       numeric_scale,
                       is_updatable
                FROM information_schema.columns
                WHERE table_schema = ?
                  AND table_name = ?
                ORDER BY ordinal_position         
                """;
        ConnectionDef connectionDef = findConnection(connectionToken);
        Connection connection = connectionDef.connection();
        try (PreparedStatement statement = connection.prepareStatement(tablesql)) {
            statement.clearParameters();
            statement.setString(1, schema);
            statement.setString(2, table);
            try (ResultSet results = statement.executeQuery()) {
                while (results.next()) {
                    columns.add(new ColumnDef(
                            results.getString("column_name"),
                            results.getString("column_default"),
                            StringUtils.equalsIgnoreCase(results.getString("is_nullable"), "YES"),
                            results.getString("data_type"),
                            results.getInt("character_maximum_length"),
                            results.getInt("numeric_precision"),
                            results.getInt("numeric_scale"),
                            StringUtils.equalsIgnoreCase(results.getString("is_updatable"), "YES")));
                }
            }
        }
        String dataSql = createDataSQL(schema, table, columns);
        try (ResultSet results = connection.prepareStatement(dataSql).executeQuery()) {
            while (results.next()) {
                List<String> values = new ArrayList<>();
                for (ColumnDef column : columns) {
                    values.add(results.getString(column.name()));
                }
                rows.add(values);
            }
        }
        return new TableDef(table, columns, rows);
    }
}
