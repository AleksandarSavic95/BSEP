package ftn.bsep9.model;


import org.kie.api.definition.type.Expires;
import org.kie.api.definition.type.Role;
import org.kie.api.definition.type.Timestamp;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

@Document
@Role(Role.Type.EVENT)
@Timestamp("getDateTimeMilliseconds()")
@Expires("2h30m")
public class Log {

    @Id
    private String id;
    @Indexed(direction = IndexDirection.ASCENDING)
    private LocalDateTime date;
    private String MACAddress;
    private String service;
    private String severityType;
    private String eventId;
    private String text;


    public Log() {}

    public Log(String logString) {
        HashMap<String, Object> resultMap = parseLog(logString);

        this.date         = (LocalDateTime) resultMap.get("date");
        this.MACAddress   = (String) resultMap.get("MACAddress");
        this.service      = (String) resultMap.get("service");
        this.severityType = (String) resultMap.get("severityType");
        this.eventId      = (String) resultMap.get("eventId");
        this.text         = (String) resultMap.get("text");
    }

    /**
     * Converts the LocalDateTime attribute to milliseconds.
     * @return calculated number of milliseconds
     */
    public long getDateTimeMilliseconds() {
        // dont ask.. https://stackoverflow.com/a/28706357/2101117
        return this.date.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
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

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
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
        String MACAddress = splittedLog[2].trim();
        String service = splittedLog[3].trim();
        // splittedLog[4] = ':'
        String severityType = splittedLog[5].trim();
        // splittedLog[6] = '-'
        String eventId = splittedLog[7]; // [number]

        StringBuilder sb = new StringBuilder();
        for (int i = 8; i < splittedLog.length; i++) {
            sb.append(splittedLog[i]).append(" ");
        }
        String text = sb.toString();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        LocalDateTime parsedDate = LocalDateTime.parse(dateTimeString, formatter);
        System.out.println(parsedDate.format(formatter));

        map.put("date", parsedDate);
        map.put("MACAddress", MACAddress);
        map.put("service", service);
        map.put("severityType", severityType);
        map.put("eventId", eventId);
        map.put("text", text);

        return map;
    }
}
