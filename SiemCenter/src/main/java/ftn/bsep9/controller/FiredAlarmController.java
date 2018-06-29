package ftn.bsep9.controller;

import ftn.bsep9.model.Alarm;
import ftn.bsep9.service.AlarmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/fired-alarms")
public class FiredAlarmController {
    @Autowired
    private AlarmService alarmService;

    @GetMapping
    public ResponseEntity<Page<Alarm>> getAll(@RequestParam(defaultValue = "0") Integer page,
                                              @RequestParam(defaultValue = "10") Integer size,
                                              @RequestParam(defaultValue = "asc") String sort) {

        Page<Alarm> fileNames = alarmService.findAllWithPages(page, size, sort);
        return new ResponseEntity<>(fileNames, HttpStatus.OK);
    }
}