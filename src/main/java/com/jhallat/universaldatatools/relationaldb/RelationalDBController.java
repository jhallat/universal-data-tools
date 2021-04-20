package com.jhallat.universaldatatools.relationaldb;

import com.jhallat.universaldatatools.exceptions.InvalidRequestException;
import com.jhallat.universaldatatools.exceptions.MissingConnectionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4201")
@RequiredArgsConstructor
@Slf4j
public class RelationalDBController {

    private final RelationalDBService relationalDBService;

    @GetMapping("/databases")
    public List<DatabaseDef> getDatabases(@RequestHeader("connection-token") String connectionToken)
            throws InvalidRequestException, SQLException, MissingConnectionException {
        if (connectionToken == null) {
            throw new InvalidRequestException("Missing connection token");
        }
        return relationalDBService.getDatabases(connectionToken);
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
}