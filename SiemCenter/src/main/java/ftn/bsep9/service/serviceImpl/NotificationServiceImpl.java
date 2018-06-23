package ftn.bsep9.service.serviceImpl;

import ftn.bsep9.model.AlarmNotification;
import ftn.bsep9.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final SimpMessagingTemplate template;

    @Autowired
    public NotificationServiceImpl(SimpMessagingTemplate template) {
        this.template = template;
    }

//    @MessageMapping("/send/message")
//    public void onReceiveMessage(String message) {
//        String dateString = new SimpleDateFormat("HH:mm:ss").format(new Date());
//        this.template.convertAndSend("/chat",
//                new AlarmNotification(dateString, message, "danger"));
//    }

    @Override
    public void sendMessage(String message) {
        String dateString = new SimpleDateFormat("HH:mm:ss").format(new Date());
        this.template.convertAndSend("/chat",
                new AlarmNotification(dateString, message, "danger"));
    }
}
