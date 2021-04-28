package com.jhallat.universaldatatools.status;

import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class StatusMessageController {

    @SendTo("/topic/status")
    public StatusMessage sendMessage(String subject, String message) {
        return new StatusMessage(subject, message);
    }

}
