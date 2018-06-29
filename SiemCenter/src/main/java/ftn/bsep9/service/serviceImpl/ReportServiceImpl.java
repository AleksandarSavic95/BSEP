package ftn.bsep9.service.serviceImpl;


import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.querydsl.core.types.dsl.BooleanExpression;
import ftn.bsep9.model.Log;
import ftn.bsep9.model.QLog;
import ftn.bsep9.model.Report;
import ftn.bsep9.model.report.AlarmMachineReportItem;
import ftn.bsep9.model.report.AlarmServiceReportItem;
import ftn.bsep9.model.report.LogMachineReportItem;
import ftn.bsep9.model.report.LogServiceReportItem;
import ftn.bsep9.repository.LogsRepository;
import ftn.bsep9.service.AlarmService;
import ftn.bsep9.service.ReportService;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private LogsRepository logsRepository;

    @Autowired
    private MongoClient autowiredMongoClient;

    @Override
    public Report generateReport(String date1, String date2, String timeReference) {
        System.out.println("Info . generate report");
        System.out.println(date1);
        System.out.println(date2);
        System.out.println(timeReference);
        System.out.println("Info . generate report asd \n");

        // 2018-06-25T11:11
        if (date1.equals("date1"))
            System.out.println("First date is not defined");

        if (date2.equals("date2"))
            System.out.println("Second date is not defined");
        String[] date1Splitted = date1.split("T");
        String[] date2Splitted = date2.split("T");

        System.out.println(date1Splitted[0] + " " + date1Splitted[1]);
        System.out.println(date2Splitted[0] + " " + date2Splitted[1]);
        LocalDateTime dateTime1 = LocalDateTime.parse(date1Splitted[0] + " " + date1Splitted[1],
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        LocalDateTime dateTime2 = LocalDateTime.parse(date2Splitted[0] + " " + date2Splitted[1],
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

        System.out.println(dateTime1); // 2018-06-21T11:11
        System.out.println(dateTime2); // 2018-06-25T23:11
        /*
            db.log.find({"date":{$gte:new ISODate("2018-05-07T23:22:22Z
            "),$lte:new ISODate("2018-05-07T23:22:59Z")}}).count();
            3
        */
        QLog qLog = new QLog("logs");
//        QAlarm qAlarm = new QAlarm("alarms");

        BooleanExpression logDateExpression;
        BooleanExpression alarmDateExpression;

        if (timeReference.equals("before")) {
            logDateExpression = qLog.date.before(dateTime1);
//            alarmDateExpression = qAlarm.date.before(dateTime1);
            System.out.println("BEFORE");
        }
        else if (timeReference.equals("after")) {
            logDateExpression = qLog.date.after(dateTime1);
//            alarmDateExpression = qAlarm.date.after(dateTime1);
            System.out.println("AFTER");
        }
        else if (timeReference.equals("between")) {
            logDateExpression = qLog.date.between(dateTime1, dateTime2);
//            alarmDateExpression = qAlarm.date.between(dateTime1, dateTime2);
            System.out.println("BETWEEN");
        }
        else {
            LocalDateTime dateTime11 = LocalDateTime.parse("2018-05-07" + " " + "21:22:22",
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            LocalDateTime dateTime22 = LocalDateTime.parse( "2018-07-07" + " " + "21:22:59",
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            logDateExpression = qLog.date.between(dateTime11, dateTime22);
        }

        System.out.println("\n System.out.println(logsRepository.count(logDateExpression));");
        System.out.println(logsRepository.count(logDateExpression));

        com.mongodb.DB db = autowiredMongoClient.getDB("logs");
        com.mongodb.DBCollection logCollection = db.getCollection("log");
        //call distinct method and store the result in list l1
        List logServicesList = logCollection.distinct("service");
        List logMachinesList = logCollection.distinct("MACAddress");
        List logSeverityTypesList = logCollection.distinct("severityType");

        Long logsCount = 0L;

        List<LogServiceReportItem> logServiceReportItems = new ArrayList<>();
        List<LogMachineReportItem> logMachineReportItems = new ArrayList<>();
        List<AlarmServiceReportItem> alarmServiceReportItems = new ArrayList<>();
        List<AlarmMachineReportItem> alarmMachineReportItems = new ArrayList<>();

        // koliko logova po servisu
        for (Object service : logServicesList) {
            System.out.println(service);
            BooleanExpression serviceExpression = qLog.service.eq(service.toString());
            logsCount = logsRepository.count(logDateExpression.and(serviceExpression));
            logServiceReportItems.add(new LogServiceReportItem(service.toString(), logsCount));
        }

        for (LogServiceReportItem lsri : logServiceReportItems ) {
            System.out.println(lsri);
        }

        // koliko logova po masini (MAC adresi)
        for(Object machine : logMachinesList) {
            System.out.println(machine);
            BooleanExpression machineExpression = qLog.MACAddress.eq(machine.toString());
            logsCount = logsRepository.count(logDateExpression.and(machineExpression));
            logServiceReportItems.add(new LogServiceReportItem(machine.toString(), logsCount));
        }

        for (LogMachineReportItem lmri : logMachineReportItems ) {
            System.out.println(lmri);
        }

//        // koliko logova po severityType-u
//        for(Object severityType : logSeverityTypesList) {
//            System.out.println(severityType);
//        }

        System.out.println("\n ALARMS");

        com.mongodb.DBCollection alarmCollection = db.getCollection("alarm");
        List alarmServicesList = alarmCollection.distinct("service");
        List alarmMachinesList = alarmCollection.distinct("MACAddress");
        List alarmSeverityTypesList = alarmCollection.distinct("severityType");

        // koliko alarma po servisu
        for (Object service : alarmServicesList) {
//            BooleanExpression serviceExpression = qAlarm.service.eq(service.toString());
//            logsCount = logsRepository.count(logDateExpression.and(serviceExpression));
//            logServiceReportItems.add(new LogServiceReportItem(service.toString(), logsCount));
            System.out.println(service);
        }

        // koliko alarma po masini (MAC adresi)
        for(Object machine : alarmMachinesList) {
            System.out.println(machine);
        }

        // koliko alarma po severityType-u
        for(Object severityType : alarmSeverityTypesList) {
            System.out.println(severityType);
        }


    // koji sve servisi postoje
    //   za svaki od njih naci broj LOGOVA u vremenskom periodu
    //   za svaki od njih naci broj ALARMA u vremenskom periodu
    // koje sve MAC Adrese postoje
    //   za svaku od njih naci broj LOGOVA u vremenskom periodu
    //   za svaku od njih naci broj ALARMA u vremenskom periodu

    // koji sve servisi postoje
    //   za svaki od njih naci broj LOGOVA u vremenskom periodu
    //   za svaki od njih naci broj ALARMA u vremenskom periodu
    // koje sve MAC Adrese postoje
    //   za svaku od njih naci broj LOGOVA u vremenskom periodu
    //   za svaku od njih naci broj ALARMA u vremenskom periodu


        return null;
    }
}
