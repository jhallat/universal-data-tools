package com.jhallat.universaldatatools.activeconnection;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4201")
@RequiredArgsConstructor
@RequestMapping("/active-connections")
public class ActiveConnectionController {

    private final ActiveConnectionService activeConnectionService;

    @GetMapping()
    public List<ActiveConnectionDTO> getAllActiveConnections() {
        return activeConnectionService.findAll();
    }

    @DeleteMapping()
    public ResponseEntity<Void> deleteAllActiveConnections() {
        activeConnectionService.removeAll();
        return ResponseEntity.noContent().build();
    }

}
