package ftn.bsep9.model.report;


public class AlarmServiceReportItem {

    private String service;
    private Long count;

    public AlarmServiceReportItem(String service, Long count) {
        this.service = service;
        this.count = count;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "ASRI: " + this.service + "; count: " + this.count;
    }
}
