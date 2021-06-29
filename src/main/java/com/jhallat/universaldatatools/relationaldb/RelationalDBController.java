package com.jhallat.universaldatatools.relationaldb;

import com.jhallat.universaldatatools.exceptions.InvalidRequestException;
import com.jhallat.universaldatatools.exceptions.MissingConnectionException;
import com.jhallat.universaldatatools.relationaldb.definition.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4201")
@RequiredArgsConstructor
@Slf4j
public class RelationalDBController {

    private final RelationalDBService relationalDBService;
    private final DataTypeService dataTypeService;

    @GetMapping("/databases")
    public List<DatabaseDef> getDatabases(@RequestHeader("connection-token") String connectionToken)
            throws InvalidRequestException, SQLException, MissingConnectionException {
        if (connectionToken == null) {
            throw new InvalidRequestException("Missing connection token");
        }
        var databases = relationalDBService.getDatabases(connectionToken);
        databases.forEach(database -> log.info("Returning database: {}", database.name()));
        return databases;
    }

    @GetMapping("/database/table/{database}/{schema}/{table}")
    public TableDef getTable(@RequestHeader("connection-token") String connectionToken,
                             @PathVariable("database") String database,
                             @PathVariable("schema") String schema,
                             @PathVariable("table") String table)
            throws InvalidRequestException, SQLException, MissingConnectionException {
        if (connectionToken == null) {
            throw new InvalidRequestException("Missing connection token");
        }
        return relationalDBService.getTable(connectionToken, database, schema, table);
    }

    @DeleteMapping("/database/{database}/table/{table}")
    public ResponseEntity<Void> deleteTable(@RequestHeader("connection-token") String connectionToken,
                     @PathVariable("database") String database,
                     @PathVariable("table") String table)
            throws InvalidRequestException, SQLException, MissingConnectionException {
        if (connectionToken == null) {
            throw new InvalidRequestException("Missing connection token");
        }
        if (relationalDBService.deleteTable(connectionToken, database, table)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/database")
    public DatabaseDef createDatabase(@RequestHeader("connection-token") String connectionToken,
                                   @RequestBody CreateDatabaseDef createDatabaseDef)
            throws InvalidRequestException, SQLException, MissingConnectionException {
        if (connectionToken == null) {
            throw new InvalidRequestException("Missing connection token");
        }
        return relationalDBService.createDatabase(connectionToken, createDatabaseDef);
    }

    @PostMapping("/database/table")
    public TableDef createTable(@RequestHeader("connection-token") String connectionToken,
                                @RequestBody CreateTableDef createTableDef)
            throws InvalidRequestException, SQLException, MissingConnectionException {
        if (connectionToken == null) {
            throw new InvalidRequestException("Missing connection token");
        }
        return relationalDBService.createTable(connectionToken, createTableDef);
    }

    // TODO the connection token will be used in the future when supporting multiple database types
    @GetMapping("/database/types")
    public List<DataTypeDef> getDatabaseTypes(@RequestHeader("connection-token") String connectionToken) {
        return dataTypeService.getDataTypes();
    }
}
