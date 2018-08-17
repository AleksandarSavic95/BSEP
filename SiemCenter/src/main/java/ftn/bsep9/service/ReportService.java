package ftn.bsep9.service;


import ftn.bsep9.model.Report;

public interface ReportService {

    Report generateReport(String date1, String date2, String timeReference);

}