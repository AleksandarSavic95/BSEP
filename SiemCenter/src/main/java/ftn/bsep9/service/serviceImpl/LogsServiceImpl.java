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

    @Autowired
    MongoOperations mongoOperations;


    public Page<Log> findAllWithPages(int pageStart, int pageSize,
                                      Sort.Direction sortDirection, String sortField) {
        PageRequest pageRequest = new PageRequest(pageStart, pageSize,
                new Sort(sortDirection, sortField));
        return this.logsRepository.findAll(pageRequest);
    }


    @Override
    public Boolean findByText(String text, Model model) {

//        List<Log> logs;
        Page<Log> logs;
        int page = 0;

        // create a query class (QLog)
        QLog qLog = new QLog("logs");

//          text=ne%C5%A1to+%3D+vrijednost+and+ne%C5%A1toDrugo+%3D+drugaVrijednost
        text = text.substring(5);  // uklonimo "text="

        text = text.replace("%5B", "[");
        text = text.replace("%5D", "]");
        text = text.replace("%3A", ":");
        text = text.replace("%3D", "=");
        text = text.replace("%5C", "\\");
        text = text.replace("+", " ");

        try {
//            text=ne%C5%A1to+%3D+vrijednost+and+ne%C5%A1toDrugo+%3D+drugaVrijednost+
//                    ne%C5%A1to+%3D+vrijednost+and+ne%C5%A1toDrugo+%3D+drugaVrijednost+

//            Document d = mongoOperations.executeCommand("find()");
//            System.out.println(d);
//            System.out.println(d.get("date"));

            // https://stackoverflow.com/a/26386715/4345461
//            final DBObject command = new BasicDBObject();
            DB db = new DB(new Mongo("localhost", 27017), "logs");
//            String collectionName = "log";
//            command.put("eval", "function() { return db." + collectionName + ".find(); }");
//            CommandResult result = db.command(command);

//            System.out.println(result);
//            System.out.println(result.get("date"));
//            System.out.println(result.get("text"));
//            for (Object o :
//                    result.values()) {
//                System.out.println(o);
//            }
//
//            for (Object o :
//                    result.keySet()) {
//                System.out.println(o);
//            }


            // Za filtriranje IZMEDJU 2 datuma
//        LocalDateTime minDateTime = LocalDateTime.now().minusHours(24L);
//        final LocalDateTime maxDateTime = LocalDateTime.now().minusSeconds(20);

            // using the query class we can create the filters
//            BooleanExpression filterByMACAddress = qLog.MACAddress.eq("\tC5:85:06:17:93:BF");
//            BooleanExpression filterByService = qLog.service.eq("\tstudent-service");
//            BooleanExpression filterByReferentService = qLog.service.eq("\treferent-service");
//            BooleanExpression filterByDate = qLog.date.between(LocalDateTime.now().minusDays(20), LocalDateTime.now());

                // we can then pass the filters to the findAll() method
//            logs = (List<Log>) this.logsRepository.findAll(
//                    filterByMACAddress.and(filterByService).or(filterByReferentService)); // filterByDate
            //,new PageRequest(page, 2, Sort.Direction.ASC, "MACAddress")

//            for (Log log :
//                    logs) {
//                System.out.println(log);
//            }


//            DBCollection collection = db.getCollection("log");
//            BasicDBObject andQuery = new BasicDBObject();
//            List<BasicDBObject> obj = new ArrayList<BasicDBObject>();
//            obj.add(new BasicDBObject("MACAddress", "\tC5:85:06:17:93:BF"));
//            obj.add(new BasicDBObject("service", "\tstudent-service"));
//            andQuery.put("$and", obj);
//
//            System.out.println(andQuery.toString());
//
//            DBCursor cursor = collection.find(andQuery);
//            while (cursor.hasNext()) {
//                System.out.println(cursor.next());
//            }


            BooleanExpression finalBooleanExpression = SearchParser.parse(text, qLog);
            logs = this.logsRepository.findAll(
                    finalBooleanExpression, new PageRequest(page, 5)); // filterByDate
             //, Sort.Direction.ASC, "MACAddress"

            for (Log log :
                    logs) {
                System.out.println(log.getText());
            }

            // Korisno za pregrage razlicitih atributa (ne samo Log.text) po regex-u ili tekstu
            //
//            Criteria criteria = Criteria.where("description").regex(searchString);
//            Query query = Query.query(criteria);
//            // apply pagination, sorting would also be specified here
//            query.limit(limit);
//            query.skip(offset);
//            mongoOperations.find(query, Movie.class);
    //        Query query = new Query();
    //        query.limit(10);
    //        query.addCriteria(Criteria.where("text").regex(Pattern.compile("")));


//            Page<Log> logsPage = logsService.findAllWithPages(page, size, Sort.Direction.ASC, "date");
            model.addAttribute("logs", logs);
            model.addAttribute("totalPages", logs.getTotalPages());
            model.addAttribute("currentPage", page);

//            model.addAttribute("logs", logs);
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
