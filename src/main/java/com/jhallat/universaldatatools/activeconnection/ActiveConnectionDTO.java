package com.jhallat.universaldatatools.activeconnection;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Setter;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ActiveConnectionDTO {

    @Setter(AccessLevel.NONE)
    private String token;
    @Setter(AccessLevel.NONE)
    private String label;
    @Setter(AccessLevel.NONE)
    private LocalDateTime lastTimeUsed;
}
