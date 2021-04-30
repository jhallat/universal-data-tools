package com.jhallat.universaldatatools.status;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
public class StatusMessageController {

    @Autowired
    private SimpMessagingTemplate simpleMessagingTemplate;

    public void sendMessage(String subject, String message) {

        log.info("{}:{}", subject, message);
        simpleMessagingTemplate.convertAndSend("/topic/status", new StatusMessage(subject, message));

    }

}
