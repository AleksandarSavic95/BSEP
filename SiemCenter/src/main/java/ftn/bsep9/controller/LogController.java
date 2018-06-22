package ftn.bsep9.controller;

import ftn.bsep9.model.Log;
import ftn.bsep9.repository.LogsRepository;
import ftn.bsep9.service.LogsService;
import org.springframework.data.domain.Sort;
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



    @GetMapping("/all")
    public String getAllNoPage(@ModelAttribute("token") String token, Model model){
        return getAll(0, model);
    }

//    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @GetMapping("/all/{page}")
    public String getAll(@PathVariable("page") int page, Model model){

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

        int size = 3;

        System.out.println("\nmodel.containsAttribute(\"token\")");
        System.out.println(model.containsAttribute("token"));

        if (logsService.findAllWithPages(model, page, size, Sort.Direction.ASC, "date")) return "logs-view";
        return "bad-request";
    }


    @PostMapping("/search-by-text/{page}")
    public String getByText(@RequestBody String text, @PathVariable("page") int page, Model model){
        if (logsService.findByText(text, page, model)) return "logs-view";
        return "bad-request";
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