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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping("/api/logs/")
public class LogController {

    private LogsService logsService;

    public LogController(LogsService logsService) {
        this.logsService = logsService;
    }

    //    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @ResponseBody
    @GetMapping("/all")
//    @PreAuthorize("hasAuthority('READ_LOGS')")
    public ResponseEntity<Page<Log>> getAll(@RequestParam Integer page,
                                            @RequestParam Integer size) {
        Page<Log> logs = logsService.findAllWithPages(page, size, Sort.Direction.ASC, "date");
        if (logs != null)
            return new ResponseEntity<>(logs, HttpStatus.OK);
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }


    @ResponseBody
    @GetMapping("/search-by-text")
//    @PreAuthorize("hasAuthority('READ_LOGS')")
    public ResponseEntity<Object> getByText(@RequestParam String searchCriteria,
                                            @RequestParam int page,
                                            @RequestParam int size) {
        Object searchRetVal = logsService.findByText(searchCriteria, page, size);
        if (String.class.isInstance(searchRetVal))
            return new ResponseEntity<>(searchRetVal, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(searchRetVal, HttpStatus.OK);
    }
}