package com.example.finance.service;

import com.example.finance.dto.ReportDtos.MonthlyReportResponse;
import com.example.finance.dto.ReportDtos.YearlyReportResponse;
import com.example.finance.entity.CategoryType;
import com.example.finance.entity.Transaction;
import com.example.finance.entity.User;
import com.example.finance.exception.ApiException;
import com.example.finance.repository.TransactionRepository;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class ReportService {
    private final TransactionRepository transactionRepository;
    private final CurrentUserService currentUserService;

    public ReportService(TransactionRepository transactionRepository, CurrentUserService currentUserService) {
        this.transactionRepository = transactionRepository;
        this.currentUserService = currentUserService;
    }

    public MonthlyReportResponse monthly(int year, int month) {
        if (month < 1 || month > 12) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Month must be between 1 and 12");
        }
        User user = currentUserService.requireCurrentUser();
        Summary summary = summarize(transactionRepository.findByMonth(user, year, month));
        return new MonthlyReportResponse(month, year, summary.income(), summary.expenses(), summary.netSavings());
    }

    public YearlyReportResponse yearly(int year) {
        User user = currentUserService.requireCurrentUser();
        Summary summary = summarize(transactionRepository.findByYear(user, year));
        return new YearlyReportResponse(year, summary.income(), summary.expenses(), summary.netSavings());
    }

    private Summary summarize(List<Transaction> transactions) {
        Map<String, BigDecimal> income = new LinkedHashMap<>();
        Map<String, BigDecimal> expenses = new LinkedHashMap<>();
        for (Transaction transaction : transactions) {
            Map<String, BigDecimal> target = transaction.getCategory().getType() == CategoryType.INCOME ? income : expenses;
            target.merge(transaction.getCategory().getName(), transaction.getAmount(), BigDecimal::add);
        }
        BigDecimal totalIncome = income.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalExpenses = expenses.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        return new Summary(income, expenses, totalIncome.subtract(totalExpenses));
    }

    private record Summary(Map<String, BigDecimal> income, Map<String, BigDecimal> expenses, BigDecimal netSavings) {
    }
}
