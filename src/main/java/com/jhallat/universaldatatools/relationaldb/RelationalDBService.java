package com.jhallat.universaldatatools.relationaldb;

import com.jhallat.universaldatatools.activeconnection.ActiveConnection;
import com.jhallat.universaldatatools.activeconnection.ActiveConnectionService;
import com.jhallat.universaldatatools.connectionlog.ConnectionLogService;
import com.jhallat.universaldatatools.exceptions.MissingConnectionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.sql.*;
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

    private ConnectionDef findConnection(String connectionToken, String dbName) throws MissingConnectionException, SQLException {
        ActiveConnection activeConnection = activeConnectionService.getConnection(connectionToken);
        if (activeConnection instanceof RelationalDBConnection connection) {
            return new ConnectionDef(connection.getLabel(), connection.getConnection(dbName));
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
        String dbSql = "SELECT datname from pg_database WHERE datistemplate = false";
        String tableSql = """
            SELECT table_type, table_name, table_schema 
            FROM information_schema.tables
            WHERE table_catalog = 'postgres' 
               OR (table_schema <> 'information_schema' AND table_schema <> 'pg_catalog')
            ORDER BY table_name   
            """;
        ConnectionDef connectionDef = findConnection(connectionToken);

        try (Connection connection = connectionDef.connection();
              ResultSet results = connection.prepareStatement(dbSql).executeQuery()) {
            while (results.next()) {
                String database = results.getString("datname");
                try (Connection tableConnection = findConnection(connectionToken, database).connection();
                     ResultSet tableResults = tableConnection.prepareStatement(tableSql).executeQuery()) {
                    while (tableResults.next()) {
                        String tableType = tableResults.getString("table_type");
                        String tableName = tableResults.getString("table_name");
                        String tableSchema = tableResults.getString("table_schema");
                        dbNames.add(database);
                        if (tableType != null) {
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
                    }
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

    public TableDef getTable(String connectionToken, String database, String schema, String table) throws SQLException, MissingConnectionException {

        List<ColumnDef> columns = new ArrayList<>();
        List<List<String>> rows = new ArrayList<>();
        String tableSql = """
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
        String keySql = String.format("""
                SELECT a.attname
                FROM   pg_index i
                JOIN   pg_attribute a ON a.attrelid = i.indrelid
                                     AND a.attnum = ANY(i.indkey)
                WHERE  i.indrelid = '%s'::regclass
                AND    i.indisprimary
                """, table);
        ConnectionDef connectionDef = findConnection(connectionToken, database);
        Connection connection = connectionDef.connection();
        String primaryKey = "";
        try (PreparedStatement tableStatement = connection.prepareStatement(tableSql);
             PreparedStatement keyStatement = connection.prepareStatement(keySql)) {
            try (ResultSet results = keyStatement.executeQuery()) {
                if (results.next()) {
                    primaryKey = results.getString("attname");
                }
            }
            tableStatement.clearParameters();
            tableStatement.setString(1, schema);
            tableStatement.setString(2, table);
            try (ResultSet results = tableStatement.executeQuery()) {
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
        return new TableDef(table, schema, columns, primaryKey, rows);
    }

    public DatabaseDef createDatabase(String connectionToken, CreateDatabaseDef databaseDef)
            throws SQLException, MissingConnectionException {
        String createSQL = String.format("CREATE DATABASE %s", databaseDef.name());
        ConnectionDef connectionDef = findConnection(connectionToken);
        Connection connection = connectionDef.connection();
        try (Statement statement = connection.createStatement()) {
            if (statement.execute(createSQL)) {
                return new DatabaseDef(databaseDef.name(), new ArrayList<>(), new ArrayList<>());
            }
        }
        return null;
    }

    private String createColumn(CreateColumnDef createColumnDef) {

        StringBuilder columnBuilder = new StringBuilder(createColumnDef.name());
        columnBuilder.append(" ");
        columnBuilder.append(createColumnDef.dataType());
        if (createColumnDef.size() > 0) {
            columnBuilder.append(String.format(" (%s) ", createColumnDef.size()));
        }
        if (createColumnDef.primaryKey()) {
            columnBuilder.append(" PRIMARY KEY ");
        } else {
            if (createColumnDef.unique()) {
                columnBuilder.append(" UNIQUE ");
            }
            if (createColumnDef.notNull()) {
                columnBuilder.append(" NOT NULL ");
            }
        }
        return columnBuilder.toString();
    }

    public TableDef createTable(String connectionToken, CreateTableDef createTableDef)
            throws SQLException, MissingConnectionException {

        String columns = createTableDef.columns().stream()
                .map(col -> createColumn(col))
                .collect(Collectors.joining(","));
        String tableName = createTableDef.name();
        if (!StringUtils.isBlank(createTableDef.schema())) {
            tableName = String.format("%s.%s", createTableDef.schema(),tableName);
        }
        String createSql = String.format("CREATE TABLE %s (%s)", tableName, columns);
        //TODO change to debug
        log.info(createSql);
        ConnectionDef connectionDef = findConnection(connectionToken, createTableDef.database());
        Connection connection = connectionDef.connection();
        try (Statement statement = connection.createStatement()) {
            if (statement.execute(createSql)) {
                log.info("Table {} created.", tableName);
                return getTable(connectionToken,
                        createTableDef.database(),
                        createTableDef.schema(),
                        createTableDef.name());
            }
        }
        return null;
    }
}
