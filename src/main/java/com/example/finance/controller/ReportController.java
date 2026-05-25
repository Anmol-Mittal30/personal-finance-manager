package com.example.finance.controller;

import com.example.finance.dto.ReportDtos.MonthlyReportResponse;
import com.example.finance.dto.ReportDtos.YearlyReportResponse;
import com.example.finance.service.ReportService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports")
public class ReportController {
    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/monthly/{year}/{month}")
    public MonthlyReportResponse monthly(@PathVariable int year, @PathVariable int month) {
        return reportService.monthly(year, month);
    }

    @GetMapping("/yearly/{year}")
    public YearlyReportResponse yearly(@PathVariable int year) {
        return reportService.yearly(year);
    }
}
