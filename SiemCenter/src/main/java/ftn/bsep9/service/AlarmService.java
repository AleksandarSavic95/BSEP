package ftn.bsep9.service;

import ftn.bsep9.model.Alarm;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public interface AlarmService {
    Alarm fireAlarm(String severity, String macAddress, String service, String text);

    Page<Alarm> findAllWithPages(int page, int size, String sortDirection);
}
