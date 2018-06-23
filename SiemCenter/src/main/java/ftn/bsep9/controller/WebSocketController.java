package ftn.bsep9.controller;

import ftn.bsep9.model.AlarmNotification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Controller only for test purposes.
 * No need to run Postman for  server -> client  message creation.
 */
@Controller
public class WebSocketController {
    private final SimpMessagingTemplate template;

    @Autowired
    public WebSocketController(SimpMessagingTemplate template) {
        this.template = template;
    }

    @MessageMapping("/send/message")
    public void onReceiveMessage(String message) {
        String dateString = new SimpleDateFormat("HH:mm:ss").format(new Date());
        this.template.convertAndSend("/chat",
                new AlarmNotification(dateString, message, "danger"));
    }
}
