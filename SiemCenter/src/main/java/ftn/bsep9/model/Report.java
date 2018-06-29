package ftn.bsep9.model;


import ftn.bsep9.model.report.AlarmMachineReportItem;
import ftn.bsep9.model.report.AlarmServiceReportItem;
import ftn.bsep9.model.report.LogMachineReportItem;
import ftn.bsep9.model.report.LogServiceReportItem;

import java.util.List;

public class Report {
    private String date1;
    private String date2;
    private String timeReference;
    private List<LogServiceReportItem> servicesList;
    private List<LogMachineReportItem> logsPerService;
    private List<AlarmServiceReportItem> alarmsPerService;
    private List<AlarmMachineReportItem> machinesList;

    public Report(String date1, String date2, String timeReference) {
        this.date1 = date1;
        this.date2 = date2;
        this.timeReference = timeReference;
    }

    public String getDate1() {
        return date1;
    }

    public void setDate1(String date1) {
        this.date1 = date1;
    }

    public String getDate2() {
        return date2;
    }

    public void setDate2(String date2) {
        this.date2 = date2;
    }

    public String getTimeReference() {
        return timeReference;
    }

    public void setTimeReference(String timeReference) {
        this.timeReference = timeReference;
    }

    public List<LogServiceReportItem> getServicesList() {
        return servicesList;
    }

    public void setServicesList(List<LogServiceReportItem> servicesList) {
        this.servicesList = servicesList;
    }

    public List<LogMachineReportItem> getLogsPerService() {
        return logsPerService;
    }

    public void setLogsPerService(List<LogMachineReportItem> logsPerService) {
        this.logsPerService = logsPerService;
    }

    public List<AlarmServiceReportItem> getAlarmsPerService() {
        return alarmsPerService;
    }

    public void setAlarmsPerService(List<AlarmServiceReportItem> alarmsPerService) {
        this.alarmsPerService = alarmsPerService;
    }

    public List<AlarmMachineReportItem> getMachinesList() {
        return machinesList;
    }

    public void setMachinesList(List<AlarmMachineReportItem> machinesList) {
        this.machinesList = machinesList;
    }
}
