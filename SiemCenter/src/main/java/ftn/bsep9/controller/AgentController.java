package ftn.bsep9.controller;

import ftn.bsep9.model.AgentReport;
import ftn.bsep9.model.Log;
import ftn.bsep9.model.Report;
import ftn.bsep9.repository.LogsRepository;
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

    /* Request = { location, [logs], timestamp } *
    @PostMapping("/reports")
    public ResponseEntity<String> receiveReport(@RequestBody Report report) {
        System.out.println("RECEIVED REPORT:\n" + report);
        return new ResponseEntity<>(HttpStatus.OK);
    } // */
    @PostMapping("/reports")
    public ResponseEntity<String> receiveAgentReport(@RequestBody AgentReport report) {
        String logString = report.getLog().substring(report.getLog().indexOf(':') + 3, report.getLog().length() - 2);
        System.out.println("RECEIVED AGENT REPORT:\n" + logString);
        Log log = new Log(logString);
        System.out.println(log);
        logsRepository.insert(log);  // CRYPTO goes here
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
