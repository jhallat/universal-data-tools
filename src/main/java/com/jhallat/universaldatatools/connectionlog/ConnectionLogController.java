package com.jhallat.universaldatatools.connectionlog;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ConnectionLogController {

    private final ConnectionLogService connectionLogService;

    @GetMapping("connection-logs")
    public List<ConnectionLog> getConnectionLogs() {
        return connectionLogService.findAll();
    }

}
