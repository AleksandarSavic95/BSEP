package ftn.bsep9.service.serviceImpl;

import com.querydsl.core.types.dsl.BooleanExpression;
import ftn.bsep9.model.Log;
import ftn.bsep9.model.QLog;
import ftn.bsep9.repository.LogsRepository;
import ftn.bsep9.service.LogsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


@Service
public class LogsServiceImpl implements LogsService {

    @Autowired
    LogsRepository logsRepository;


    public Page<Log> findAllWithPages(int pageStart, int pageSize,
                                      Sort.Direction sortDirection, String sortField) {
        PageRequest pageRequest = new PageRequest(pageStart, pageSize,
                new Sort(sortDirection, sortField));
        return this.logsRepository.findAll(pageRequest);
    }


    @Override
    public Boolean findByText(String text, Model model) {

        try {
            System.out.println(text);
//      optradio=regex&text=.*logged in.*
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

            model.addAttribute("method-url", "/api/search-by-text");
            model.addAttribute("logs", logs);
            model.addAttribute("searchedString", text);
        }
        catch (Exception e) {
            e.printStackTrace();
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
}
