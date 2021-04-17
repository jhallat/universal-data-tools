package com.jhallat.universaldatatools.relationaldb;

import com.jhallat.universaldatatools.exceptions.InvalidRequestException;
import com.jhallat.universaldatatools.exceptions.MissingConnectionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.persistence.Table;
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

    @GetMapping("/database/table/{schema}/{table}")
    public TableDef getTable(@RequestHeader("connection-token") String connectionToken,
                             @PathVariable("schema") String schema,
                             @PathVariable("table") String table)
            throws InvalidRequestException, SQLException, MissingConnectionException {
        if (connectionToken == null) {
            throw new InvalidRequestException("Missing connection token");
        }
        return relationalDBService.getTable(connectionToken, schema, table);
    }
}
