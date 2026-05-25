package com.example.finance.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.example.finance.dto.ReportDtos.MonthlyReportResponse;
import com.example.finance.entity.Category;
import com.example.finance.entity.CategoryType;
import com.example.finance.entity.Transaction;
import com.example.finance.entity.User;
import com.example.finance.repository.TransactionRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {
    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private CurrentUserService currentUserService;

    private ReportService service;
    private User user;

    @BeforeEach
    void setUp() {
        service = new ReportService(transactionRepository, currentUserService);
        user = new User("user@example.com", "password", "User", "+1234567890");
    }

    @Test
    void monthlyReportGroupsIncomeExpensesAndNetSavings() {
        Category salary = new Category("Salary", CategoryType.INCOME, false, null);
        Category food = new Category("Food", CategoryType.EXPENSE, false, null);
        when(currentUserService.requireCurrentUser()).thenReturn(user);
        when(transactionRepository.findByMonth(user, 2024, 1)).thenReturn(List.of(
                new Transaction(BigDecimal.valueOf(3000), LocalDate.of(2024, 1, 10), salary, "pay", user),
                new Transaction(BigDecimal.valueOf(400), LocalDate.of(2024, 1, 12), food, "lunch", user)));

        MonthlyReportResponse report = service.monthly(2024, 1);

        assertThat(report.totalIncome()).containsEntry("Salary", BigDecimal.valueOf(3000));
        assertThat(report.totalExpenses()).containsEntry("Food", BigDecimal.valueOf(400));
        assertThat(report.netSavings()).isEqualByComparingTo("2600");
    }
}
