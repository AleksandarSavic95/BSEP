package ftn.bsep9.service;

import ftn.bsep9.model.Alarm;
import org.springframework.stereotype.Service;

@Service
public interface AlarmService {
    Alarm fireAlarm(Alarm alarm);
}
