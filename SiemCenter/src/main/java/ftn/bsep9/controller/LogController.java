package ftn.bsep9.controller;

import com.querydsl.core.types.dsl.BooleanExpression;
import ftn.bsep9.model.Log;
import ftn.bsep9.model.QLog;
import ftn.bsep9.repository.LogsRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Controller
@RequestMapping("/api")
public class LogController {

    private LogsRepository logsRepository;

    public LogController(LogsRepository logsRepository) {
        this.logsRepository = logsRepository;
    }


    @GetMapping("/all")
    public String getAll(Model model){
        List<Log> logs = this.logsRepository.findAll();

        model.addAttribute("logs", logs);
        return "logs-view";
    }


    @PostMapping("/insert")
    public String insertOne(@RequestBody Log log, Model model) {
        Log insertedLog = logsRepository.insert(log);

        model.addAttribute("log", insertedLog);
        return "log-view";
    }


    @PutMapping
    public void update(@RequestBody Log logs){
        this.logsRepository.save(logs);
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


    @GetMapping("/number/{someNumber}")
    public String getBySomeNumber(@PathVariable("someNumber") int someNumber, Model model){
        List<Log> logs = this.logsRepository.findBySomeNumber(someNumber);

        model.addAttribute("logs", logs);
        return "logs-view";
    }


    @GetMapping("/date/{date}")
    public String getByDate(@PathVariable("date") Date date, Model model){
        List<Log> logs = this.logsRepository.findByDate(date);

        model.addAttribute("logs", logs);
        return "logs-view";
    }


    @PostMapping("/text")
    public String getByText(@RequestBody String text, Model model){
        text = text.substring(text.indexOf('=') + 1);
        text = text.replace('+',' ');

        // create a query class (QLog)
        QLog qLog = new QLog("log");

        // using the query class we can create the filters
        BooleanExpression filterByCountry = qLog.text.containsIgnoreCase(text);

        // we can then pass the filters to the findAll() method
        List<Log> logs = (List<Log>) this.logsRepository.findAll(filterByCountry);

        model.addAttribute("logs", logs);
        model.addAttribute("searchedString", text);
        return "logs-view";
    }

    @GetMapping("/recommended")
    public String getBySomeNumber(Model model){
        final int minSomeNumber = 7;
        final int maxSomeNumber = 100;

        // create a query class (QLog)
        QLog qLog = new QLog("logs");

        // using the query class we can create the filters
        BooleanExpression filterByMinSomeNumber = qLog.someNumber.gt(minSomeNumber);
        BooleanExpression filterByMaxSomeNumber = qLog.someNumber.lt(maxSomeNumber);

        // we can then pass the filters to the findAll() method
        List<Log> logs = (List<Log>) this.logsRepository.findAll(filterByMinSomeNumber.and(filterByMaxSomeNumber));

        model.addAttribute("logs", logs);
        return "logs-view";
    }


}