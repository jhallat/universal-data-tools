package com.jhallat.universaldatatools.connectionlog;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "connection_log")
@NoArgsConstructor
@AllArgsConstructor
public class ConnectionLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String connection;
    private String logType;
    private LocalDateTime logTimestamp;
    private String message;

}
