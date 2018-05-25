package ftn.bsep9.service.serviceImpl;

import com.mongodb.*;
import com.querydsl.core.types.dsl.BooleanExpression;
import ftn.bsep9.model.Log;
import ftn.bsep9.model.QLog;
import ftn.bsep9.repository.LogsRepository;
import ftn.bsep9.service.LogsService;
import ftn.bsep9.utility.SearchParser;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.*;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static ftn.bsep9.utility.SearchParser.parse;


@Service
public class LogsServiceImpl implements LogsService {

    @Autowired
    LogsRepository logsRepository;


    public Boolean findAllWithPages(Model model, int page, int size,
                                      Sort.Direction sortDirection, String sortField) {

        try {
            Long count = this.logsRepository.count();
            page = checkPageRange(page, count / size, model);

            PageRequest pageRequest = new PageRequest(page, size,
                    new Sort(sortDirection, sortField));

            Page<Log> logs = this.logsRepository.findAll(pageRequest);

            model.addAttribute("logs", logs);
            model.addAttribute("totalPages", logs.getTotalPages());
            model.addAttribute("currentPage", page);
        }
        catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("title", "Bad Request");
            return false;
        }

        return true;
    }


    @Override
    public Boolean findByText(String text, int page, Model model) {

        int size = 3;
        Page<Log> logs;
        QLog qLog = new QLog("logs");  // create a query class (QLog)

//      text=ne%C5%A1to+%3D+vrijednost+and+ne%C5%A1toDrugo+%3D+drugaVrijednost
        text = text.substring(5);  // uklonimo "text="
        text = text.replace("%2B", " ");  // "+" u "razmak"
        text = text.replace("%3A", ":");
        text = text.replace("%26", "&");

        model.addAttribute("searchString", text);  // dodamo pretragu po tekstu u promjenljivu za sljedecu stranicu

        text = text.replace("%5B", "[");
        text = text.replace("%5D", "]");
        text = text.replace("%3D", "=");
        text = text.replace("%5C", "\\");
        text = text.replace("%2F", "/");
        text = text.replace("+", " ");

        try {
            BooleanExpression finalBooleanExpression = SearchParser.parse(text, qLog);

            Long count = this.logsRepository.count(finalBooleanExpression);
            page = checkPageRange(page, count / size, model);

            logs = this.logsRepository.findAll(
                    finalBooleanExpression, new PageRequest(page, size)); // filterByDate
             //, Sort.Direction.ASC, "MACAddress"

            model.addAttribute("logs", logs);
            model.addAttribute("totalPages", logs.getTotalPages());
            model.addAttribute("currentPage", page);
            model.addAttribute("searchedString", text);
        }
        catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("title", "Bad Request");
            return false;
        }

        return true;
    }


    @Override
    public Boolean findByDate(String text, Model model) {
        try {
            System.out.println(text);
//      optradio=before&date-1=2018-05-08T01%3A40
//      optradio=after&date-1=2018-05-08T01%3A10
//        optradio=between&date-1=2018-05-03T11%3A11&date-2=2018-05-11T23%3A11
//        optradio=between&date-1=2018-05-03T11%3A10&date-2=2018-05-11T23%3A25

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

            // Za filtriranje IZMEDJU 2 datuma
//        LocalDateTime minDateTime = LocalDateTime.now().minusHours(24L);
//        final LocalDateTime maxDateTime = LocalDateTime.now().minusSeconds(20);

            // using the query class we can create the filters
//        BooleanExpression filterByMinDate = qLog.date.gt(minDateTime);
//        BooleanExpression filterByMaxDate = qLog.date.lt(maxDateTime);

            // we can then pass the filters to the findAll() method
//        List<Log> logs = (List<Log>) this.logsRepository.findAll(filterByMinDate.and(filterByMaxDate));

            model.addAttribute("method-url", "/api/search-by-date");
            model.addAttribute("logs", logs);
            String searchedString = "logs dating " + beforeAfterString + " " + prettyDateTime;
            model.addAttribute("searchedString", searchedString);
        }
        catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("title", "Bad Request");
            return false;
        }

        return true;
    }


    private int checkPageRange(int page, Long totalPages, Model model) {
        if (page < 0 || page > totalPages) {
            page = 0;
            model.addAttribute("pageOutOfRange", true);
        }

        return page;
    }
}

