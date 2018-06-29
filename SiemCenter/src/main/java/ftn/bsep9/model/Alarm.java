package ftn.bsep9.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document
public class Alarm {
    @Id
    private String id;
    private String severity;  // machine
    private String macAddress;
    private LocalDateTime dateTime;
    private String service;   // system
    private String text;

    public Alarm() {
        this.dateTime = LocalDateTime.now();
    }

    public Alarm(String severity, String macAddress, String service, String text) {
        this.dateTime = LocalDateTime.now();
        this.severity = severity;
        this.macAddress = macAddress;
        this.service = service;
        this.text = text;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "Alarm{" +
                "id='" + id + '\'' +
                ", severity='" + severity + '\'' +
                ", macAddress='" + macAddress + '\'' +
                ", dateTime=" + dateTime +
                ", service='" + service + '\'' +
                '}';
    }
}
