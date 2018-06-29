package ftn.bsep9.service.serviceImpl;

import ftn.bsep9.model.Alarm;
import ftn.bsep9.repository.AlarmRepository;
import ftn.bsep9.service.AlarmService;
import ftn.bsep9.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class AlarmServiceImpl implements AlarmService {
    @Autowired
    private NotificationService notificationService;

    @Autowired
    private AlarmRepository alarmRepository;

    @Override
    public Alarm fireAlarm(String severity, String macAddress, String service, String text) {
        notificationService.sendMessage(text, severity);

        Alarm alarm = new Alarm(severity, macAddress, service, text);
        alarm = alarmRepository.insert(alarm);

        System.out.println("saved alarm object: " + alarm);
        return alarm;
    }

    public Page<Alarm> findAllWithPages(int page, int size, String sort) {
        Page<Alarm> firedAlarms;
        Sort.Direction sortDirection = sort.equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;

        try {
            Long count = this.alarmRepository.count();
            if (page < 0 || page > count / size)
                page = 0;

            PageRequest pageRequest = PageRequest.of(page, size, Sort.by(sortDirection, "dateTime"));
            firedAlarms = this.alarmRepository.findAll(pageRequest);

            System.out.println("total found: " + count);
            System.out.println("total on page " + page + " >>> " + firedAlarms.getSize());
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            return null;
        }

        return firedAlarms;
    }
}
