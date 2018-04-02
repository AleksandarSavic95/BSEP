package ftn.bsep9.model;

import java.util.ArrayList;
import java.util.Date;

public class Report {
    private String location;
    private ArrayList<String> logs;
    private Date timestamp;

    public Report() {
        this.logs = new ArrayList<>();
    }

    public Report(String location, Date timestamp) {
        this.location = location;
        this.logs = new ArrayList<>();
        this.timestamp = timestamp;
    }

    public Report(String location, ArrayList<String> logs, Date timestamp) {
        this.location = location;
        this.logs = logs;
        this.timestamp = timestamp;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public ArrayList<String> getLogs() {
        return logs;
    }

    public void setLogs(ArrayList<String> logs) {
        this.logs = logs;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
