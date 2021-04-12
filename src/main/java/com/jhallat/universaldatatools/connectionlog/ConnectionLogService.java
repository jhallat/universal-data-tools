package com.jhallat.universaldatatools.connectionlog;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ConnectionLogService {

    private final ConnectionLogRepository connectionLogRepository;

    public List<ConnectionLog> findAll() {
        return connectionLogRepository.findAll();
    }

    public void info(String connection, String message, Object...args) {
        ConnectionLog connectionLog = new ConnectionLog();
        connectionLog.setConnection(connection);
        connectionLog.setLogType("info");
        connectionLog.setLogTimestamp(LocalDateTime.now());
        connectionLog.setMessage(String.format(message, args));
        connectionLogRepository.save(connectionLog);
    }

    public void error(String connection, String message, Object...args) {
        ConnectionLog connectionLog = new ConnectionLog();
        connectionLog.setConnection(connection);
        connectionLog.setLogType("error");
        connectionLog.setLogTimestamp(LocalDateTime.now());
        connectionLog.setMessage(String.format(message, args));
        connectionLogRepository.save(connectionLog);
    }

}
