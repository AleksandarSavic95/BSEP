package ftn.bsep9.service.serviceImpl;

import ftn.bsep9.model.Alarm;
import ftn.bsep9.repository.AlarmRepository;
import ftn.bsep9.service.AlarmService;
import ftn.bsep9.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AlarmServiceImpl implements AlarmService {
    @Autowired
    private NotificationService notificationService;

    @Autowired
    private AlarmRepository alarmRepository;

    @Override
    public Alarm fireAlarm(Alarm alarm) {
        notificationService.sendMessage(alarm.getText(), alarm.getSeverity());
        alarm = alarmRepository.insert(alarm);
        return alarm;
    }
}
