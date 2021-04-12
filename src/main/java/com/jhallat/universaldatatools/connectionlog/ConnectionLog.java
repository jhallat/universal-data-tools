package com.jhallat.universaldatatools.connectionlog;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "connection_log")
@NoArgsConstructor
@AllArgsConstructor
public class ConnectionLog {

    @Id
    private long id;
    private String connection;
    private String logType;
    private LocalDateTime logTimestamp;
    private String message;

}
