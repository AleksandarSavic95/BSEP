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

//        logsPage.getSize()              2
//        logsPage.getNumberOfElements()  2
//        logsPage.getNumber()            0
//        logsPage.getTotalElements()     5
//        logsPage.getTotalPages());      3
//        logsPage.getContent());
//        [Log{id='5af0dfb455b9f02858a61b07', ...
//        logsPage.getPageable());                   Page request [number: 0, size 2, sort: UNSORTED]
//        logsPage.getPageable().getOffset());       0
//        logsPage.getPageable().getPageNumber());   0
//        logsPage.getPageable().getPageSize());     2

        Page<Log> logs = logsService.findAllWithPages(page, size, Sort.Direction.ASC, "date");
        if (logs != null)
            return new ResponseEntity<>(logs, HttpStatus.OK);
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }


    @ResponseBody
    @GetMapping("/search-by-text")
    public ResponseEntity<Page<Log>> getByText(@RequestParam String searchCriteria,
                                               @RequestParam int page,
                                               @RequestParam int size) {

        System.out.println("");
        System.out.println(searchCriteria);
        System.out.println(page);
        System.out.println(size);
        Page<Log> logsPage = logsService.findByText(searchCriteria, page, size);
        if (logsPage != null)
            return new ResponseEntity<>(logsPage, HttpStatus.OK);
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }


//    @PostMapping("/search-by-date")
//    public String searchByDate(@RequestBody String text, Model model){
//        if (logsService.findByDate(text, model)) return "logs-view";
//        return "bad-request";
//    }


    @PostMapping("/insert")
    public String insertOne(@RequestBody Log log, Model model) {
        Log insertedLog = logsRepository.insert(log);

        model.addAttribute("log", insertedLog);
        return "log-view";
    }


//    @PutMapping
//    public void update(@RequestBody Log log){
//        this.logsRepository.save(log);
//    }


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


//    @GetMapping("/date/{date}")
//    public String getByDate(@PathVariable("date") String dateString, Model model){
//        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
//        LocalDateTime localDateTime = LocalDateTime.parse(dateString, dateTimeFormatter);
//        List<Log> logs = this.logsRepository.findByDate(localDateTime);
//
//        model.addAttribute("logs", logs);
//        return "logs-view";
//    }

//    @GetMapping("/recent")
//    public String getByDateRecent(Model model){
//        LocalDateTime minDateTime = LocalDateTime.now().minusHours(24L);
//        final LocalDateTime maxDateTime = LocalDateTime.now().minusSeconds(20);
//
//        // create a query class (QLog)
//        QLog qLog = new QLog("logs");
//
//        // using the query class we can create the filters
//        BooleanExpression filterByMinDate = qLog.date.gt(minDateTime);
//        BooleanExpression filterByMaxDate = qLog.date.lt(maxDateTime);
//
//        // we can then pass the filters to the findAll() method
//        List<Log> logs = (List<Log>) this.logsRepository.findAll(filterByMinDate.and(filterByMaxDate));
//
//        model.addAttribute("logs", logs);
//        return "logs-view";
//    }


}