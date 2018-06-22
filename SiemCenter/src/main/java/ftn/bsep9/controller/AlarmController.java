package ftn.bsep9.controller;

import ftn.bsep9.model.AlarmFile;
import ftn.bsep9.service.AlarmStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/alarms")
public class AlarmController {

    @Autowired
    private AlarmStorageService alarmStorageService;

    @PostMapping()
    public ResponseEntity<String> receiveAgentReport(@RequestBody AlarmFile alarmFile) {
        System.out.println(alarmFile);
        alarmStorageService.saveAlarm(alarmFile);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
