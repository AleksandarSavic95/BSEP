package ftn.bsep9.controller;

import ftn.bsep9.model.Log;
import ftn.bsep9.repository.LogsRepository;
import ftn.bsep9.service.LogsService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;


@Controller
@RequestMapping("/api/logs/")
public class LogController {

    private LogsRepository logsRepository;

    private LogsService logsService;

    public LogController(LogsRepository logsRepository, LogsService logsService) {
        this.logsRepository = logsRepository;
        this.logsService = logsService;
    }


//    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @ResponseBody
    @GetMapping("/all")
    public ResponseEntity<Page<Log>> getAll(@RequestParam Integer page,
                                            @RequestParam Integer size) {
        Page<Log> logs = logsService.findAllWithPages(page, size, Sort.Direction.ASC, "date");
        if (logs != null)
            return new ResponseEntity<>(logs, HttpStatus.OK);
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }


    @ResponseBody
    @GetMapping("/search-by-text")
    public ResponseEntity<Object> getByText(@RequestParam String searchCriteria,
                                               @RequestParam int page,
                                               @RequestParam int size) {
        Object searchRetVal = logsService.findByText(searchCriteria, page, size);
        if (String.class.isInstance(searchRetVal))
            return new ResponseEntity<>(searchRetVal, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(searchRetVal, HttpStatus.OK);
    }


    @PostMapping("/insert")
    public String insertOne(@RequestBody Log log, Model model) {
        Log insertedLog = logsRepository.insert(log);

        model.addAttribute("log", insertedLog);
        return "log-view";
    }


    @DeleteMapping("/delete")
    public String deleteAll(Model model) {
        logsRepository.deleteAll();

        model.addAttribute("logs", new ArrayList<Log>());
        return "logs-view";
    }


    @GetMapping("/id/{id}")
    public String getById(@PathVariable("id") String id, Model model){
        Log log = this.logsRepository.findById(id).get();

        model.addAttribute("log", log);
        model.addAttribute("title", "Log view");
        return "log-view";
    }

}