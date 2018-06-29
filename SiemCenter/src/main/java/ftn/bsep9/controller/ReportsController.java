package ftn.bsep9.controller;


import ftn.bsep9.model.Report;
import ftn.bsep9.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports")
public class ReportsController {

    @Autowired
    private ReportService reportService;


    @GetMapping("/generate")
    public ResponseEntity<Report> generateReport(@RequestParam(defaultValue = "date1") String date1,
                                         @RequestParam(defaultValue = "date2") String date2,
                                         @RequestParam(defaultValue = "timeReference") String timeReference) {
        System.out.println("Info . Report Controller generate report");
        System.out.println(date1);
        System.out.println(date2);
        System.out.println(timeReference);
        Report report = reportService.generateReport(date1, date2, timeReference);
        return new ResponseEntity<>(report, HttpStatus.OK);
    }
}
