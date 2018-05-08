package ftn.bsep9.controller;

import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.querydsl.core.types.dsl.BooleanExpression;
import ftn.bsep9.model.Log;
import ftn.bsep9.model.QLog;
import ftn.bsep9.repository.LogsRepository;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;


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
    public void update(@RequestBody Log log){
        this.logsRepository.save(log);
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


//    @GetMapping("/number/{someNumber}")
//    public String getBySomeNumber(@PathVariable("someNumber") int someNumber, Model model){
//        List<Log> logs = this.logsRepository.findBySomeNumber(someNumber);
//
//        model.addAttribute("logs", logs);
//        return "logs-view";
//    }


    @GetMapping("/date/{date}")
    public String getByDate(@PathVariable("date") String dateString, Model model){
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        LocalDateTime localDateTime = LocalDateTime.parse(dateString, dateTimeFormatter);
        List<Log> logs = this.logsRepository.findByDate(localDateTime);

        model.addAttribute("logs", logs);
        return "logs-view";
    }


    @PostMapping("/text")
    public String getByText(@RequestBody String text, Model model){
        System.out.println(text);
//      optradio=regex&text=.*filip.*
//      optradio=text&text=logged+in

        String[] regexAndTextList = text.split("&");
        String regexTextString = regexAndTextList[0].split("=")[1];
        System.out.println(regexTextString);
        Boolean regex = regexTextString.equals("regex");

        List<Log> logs;
        if (regex) {
            System.out.println("REGEX");
            text = regexAndTextList[1].substring(regexAndTextList[1].indexOf('=') + 1);
            logs = logsRepository.findByTextRegexIgnoreCase(text);
        }
        else {
            System.out.println("TEXT");
            text = regexAndTextList[1].substring(regexAndTextList[1].indexOf('=') + 1);
            text = text.replace('+', ' ');
            logs = this.logsRepository.findAllByTextContainsIgnoreCase(text);
        }

        // Korisno za pregrage razlicitih atributa (ne samo Log.text) po regex-u ili tekstu
        //
//        import org.springframework.data.mongodb.core.query.*;
//        import org.springframework.data.mongodb.core.MongoOperations;
//
//        public List<Movie> searchInDescription(String searchString, int limit, int offset) {
//            Criteria criteria = Criteria.where("description").regex(searchString);
//            Query query = Query.query(criteria);
//            // apply pagination, sorting would also be specified here
//            query.limit(limit);
//            query.skip(offset);
//            return mongoOperations.find(query, Movie.class);
//        }
//        Query query = new Query();
//        query.limit(10);
//        query.addCriteria(Criteria.where("text").regex(Pattern.compile("")));

        model.addAttribute("logs", logs);
        model.addAttribute("searchedString", text);
        return "logs-view";
    }

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


    @PostMapping("/search-by-date")
    public String searchByDate(@RequestBody String text, Model model){
        System.out.println(text);
//      optradio=before&date-1=2018-05-08T01%3A40
//      optradio=after&date-1=2018-05-08T01%3A10

        String[] beforeAndDateList = text.split("&");
        String beforeAfterString = beforeAndDateList[0].split("=")[1];
        Boolean before = beforeAfterString.equals("before");

        String dateTimeString = beforeAndDateList[1].split("=")[1]; // dateString = 2018-05-08T01%3A40
        String dateString = dateTimeString.split("T")[0];  // 2018-05-08
        String timeString = dateTimeString.split("T")[1];  // 01%3A40
        String hoursString = timeString.split("%3A")[0];   // 01
        String minutesString = timeString.split("%3A")[1]; // 40
        dateTimeString = dateString + " " + hoursString + ":" + minutesString; // 2018-05-08 01:40

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime dateTime = LocalDateTime.parse(dateTimeString, formatter);
        String prettyDateTime = dateTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
        System.out.println(prettyDateTime);

        // create a query class (QLog)
        QLog qLog = new QLog("logs");

        BooleanExpression filterByDate;
        if (before) filterByDate = qLog.date.lt(dateTime);
        else filterByDate = qLog.date.gt(dateTime);

        List<Log> logs = (List<Log>) this.logsRepository.findAll(filterByDate);

//        LocalDateTime minDateTime = LocalDateTime.now().minusHours(24L);
//        final LocalDateTime maxDateTime = LocalDateTime.now().minusSeconds(20);

        // using the query class we can create the filters
//        BooleanExpression filterByMinDate = qLog.date.gt(minDateTime);
//        BooleanExpression filterByMaxDate = qLog.date.lt(maxDateTime);

        // we can then pass the filters to the findAll() method
//        List<Log> logs = (List<Log>) this.logsRepository.findAll(filterByMinDate.and(filterByMaxDate));

        model.addAttribute("logs", logs);
        String searchedString = "logs dating " + beforeAfterString + " " + prettyDateTime;
        model.addAttribute("searchedString", searchedString);
        return "logs-view";
    }


}