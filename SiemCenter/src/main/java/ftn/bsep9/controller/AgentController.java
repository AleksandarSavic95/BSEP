package ftn.bsep9.controller;

import ftn.bsep9.model.AgentReport;
import ftn.bsep9.model.Log;
import ftn.bsep9.model.Report;
import ftn.bsep9.repository.LogsRepository;
import ftn.bsep9.service.LogsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AgentController {
    @Autowired
    LogsRepository logsRepository;

    @Autowired
    LogsService logsService;

    @PostMapping("/reports")
    public ResponseEntity<String> receiveAgentReport(@RequestBody AgentReport report) {
        Log log = new Log(report.getLog());
        System.out.println(log);
        logsRepository.insert(log);  // CRYPTO goes here
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/logs")
    public ResponseEntity<String> receiveLogKBS(@RequestBody AgentReport report) {
        Log log = new Log(report.getLog());
        System.out.println(log);

        logsService.saveLog(log);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
