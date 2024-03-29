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

    @Override
    public void sendWarning(String message) {
        this.sendMessage(message, "warning");
    }

    @Override
    public void sendInfo(String message) {
        this.sendMessage(message, "info");
    }

    @Override
    public void sendMessage(String message, String level) {
        String dateString = new SimpleDateFormat("HH:mm:ss").format(new Date());
        System.out.println("sending message " + message);
        this.template.convertAndSend("/chat",
                new AlarmNotification(dateString, message, level));
    }
}
