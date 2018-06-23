package ftn.bsep9.service;

import org.springframework.stereotype.Service;

@Service
public interface NotificationService {
    void sendMessage(String message);
}
