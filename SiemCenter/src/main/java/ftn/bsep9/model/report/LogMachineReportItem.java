package ftn.bsep9.model.report;


public class LogMachineReportItem {

    private String MACAddress;
    private Long count;

    public LogMachineReportItem(String MACAddress, Long count) {
        this.MACAddress = MACAddress;
        this.count = count;
    }

    public String getMACAddress() {
        return MACAddress;
    }

    public void setMACAddress(String MACAddress) {
        this.MACAddress = MACAddress;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "LMRI: " + this.MACAddress + " ; count: " + this.count;
    }
}
