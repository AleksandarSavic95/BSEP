package ftn.bsep9.model;


import ftn.bsep9.model.report.AlarmMachineReportItem;
import ftn.bsep9.model.report.AlarmServiceReportItem;
import ftn.bsep9.model.report.LogMachineReportItem;
import ftn.bsep9.model.report.LogServiceReportItem;

import java.util.List;

public class Report {
    private List<LogServiceReportItem> logsPerService;
    private List<LogMachineReportItem> logsPerMachine;
    private List<AlarmServiceReportItem> alarmsPerService;
    private List<AlarmMachineReportItem> alarmsPerMachine;

    public Report(List<LogServiceReportItem> logServiceReportItems, List<LogMachineReportItem> logMachineReportItems,
            List<AlarmServiceReportItem> alarmServiceReportItems, List<AlarmMachineReportItem> alarmMachineReportItems) {
        this.logsPerService = logServiceReportItems;
        this.logsPerMachine = logMachineReportItems;
        this.alarmsPerService = alarmServiceReportItems;
        this.alarmsPerMachine = alarmMachineReportItems;
    }

    public List<LogServiceReportItem> getLogsPerService() {
        return logsPerService;
    }

    public void setLogsPerService(List<LogServiceReportItem> logsPerService) {
        this.logsPerService = logsPerService;
    }

    public List<LogMachineReportItem> getLogsPerMachine() {
        return logsPerMachine;
    }

    public void setLogsPerMachine(List<LogMachineReportItem> logsPerMachine) {
        this.logsPerMachine = logsPerMachine;
    }

    public List<AlarmMachineReportItem> getAlarmsPerMachine() {
        return alarmsPerMachine;
    }

    public void setAlarmsPerMachine(List<AlarmMachineReportItem> alarmsPerMachine) {
        this.alarmsPerMachine = alarmsPerMachine;
    }

    public List<AlarmServiceReportItem> getAlarmsPerService() {
        return alarmsPerService;
    }

    public void setAlarmsPerService(List<AlarmServiceReportItem> alarmsPerService) {
        this.alarmsPerService = alarmsPerService;
    }
}
