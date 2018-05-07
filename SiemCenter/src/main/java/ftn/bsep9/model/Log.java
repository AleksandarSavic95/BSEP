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
    private LocalDateTime date;
    private String MACAddress;
    private String service;
    private String severityType;
    private String text;


    /*
    * Log{
    * id='5af06c6955b9f037bc823efc',
    * text='{ "log":
    * "07-05-2018 17:10:30 C5:85:06:17:93:BF professor-service : WARNING - [1525705830814] User with username: coa995 has logged in. "}', date=null, someNumber=null}
    * */
    public Log(String logString) {
        HashMap<String, Object> resultMap = parseLog(logString);

        this.date = (LocalDateTime) resultMap.get("date");
        this.MACAddress = (String) resultMap.get("MACAddress");
        this.service = (String) resultMap.get("service");
        this.severityType = (String) resultMap.get("severityType");
        this.text = (String) resultMap.get("text");
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public Integer getSomeNumber() {
        return someNumber;
    }

    public void setSomeNumber(Integer someNumber) {
        this.someNumber = someNumber;
    }

    @Indexed(direction = IndexDirection.ASCENDING)
    private Integer someNumber;

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


    @Override
    public String toString() {
        return "Log{" +
                "id='" + id + '\'' +
                ", text='" + text + '\'' +
                ", date=" + date +
                ", someNumber=" + someNumber +
                '}';
    }


    private HashMap<String, Object> parseLog(String logString) {
        HashMap<String, Object> map = new HashMap<String, Object>();

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

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy : HH:mm:ss");
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
