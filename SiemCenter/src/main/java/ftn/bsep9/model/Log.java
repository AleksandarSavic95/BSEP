package ftn.bsep9.model;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

@Document
public class Log {

    @Id
    private String id;
    @Indexed(direction = IndexDirection.ASCENDING)
    private LocalDateTime date;
    private String MACAddress;
    private String service;
    private String severityType;
    private String text;


    public Log() {}

    public Log(String logString) {
        HashMap<String, Object> resultMap = parseLog(logString);

        this.date         = (LocalDateTime) resultMap.get("date");
        this.MACAddress   = (String) resultMap.get("MACAddress");
        this.service      = (String) resultMap.get("service");
        this.severityType = (String) resultMap.get("severityType");
        this.text         = (String) resultMap.get("text");
    }


    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getMACAddress() {
        return MACAddress;
    }

    public void setMACAddress(String MACAddress) {
        this.MACAddress = MACAddress;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getSeverityType() {
        return severityType;
    }

    public void setSeverityType(String severityType) {
        this.severityType = severityType;
    }


    @Override
    public String toString() {
        return "Log{" +
                "id='" + id + '\'' +
                ", date=" + date +
                ", MACAddress='" + MACAddress + '\'' +
                ", service='" + service + '\'' +
                ", severityType='" + severityType + '\'' +
                ", text='" + text +
                '}';
    }


    private HashMap<String, Object> parseLog(String logString) {
        HashMap<String, Object> map = new HashMap<>();

        String[] splittedLog = logString.split(" ");
        String dateTimeString = splittedLog[0] + " " + splittedLog[1];
        String MACAddress = splittedLog[2];
        String service = splittedLog[3];
        String severityType = splittedLog[5]; // splittedLog[4] = ' : '

        StringBuilder sb = new StringBuilder();
        for (int i = 6; i < splittedLog.length; i++) {
            String remainingText = splittedLog[i] + " ";
            sb.append(remainingText);
        }
        String text = sb.toString();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        LocalDateTime parsedDate = LocalDateTime.parse(dateTimeString, formatter);
        System.out.println(parsedDate.format(formatter));

        map.put("date", parsedDate);
        map.put("MACAddress", MACAddress);
        map.put("service", service);
        map.put("severityType", severityType);
        map.put("text", text);

        return map;
    }

}
