package com.example.finance.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.example.finance.dto.GoalDtos.GoalResponse;
import com.example.finance.entity.Category;
import com.example.finance.entity.CategoryType;
import com.example.finance.entity.SavingsGoal;
import com.example.finance.entity.Transaction;
import com.example.finance.entity.User;
import com.example.finance.repository.SavingsGoalRepository;
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
class GoalServiceTest {
    @Mock
    private SavingsGoalRepository goalRepository;
    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private CurrentUserService currentUserService;

    private GoalService service;
    private User user;

    @BeforeEach
    void setUp() {
        service = new GoalService(goalRepository, transactionRepository, currentUserService);
        user = new User("user@example.com", "password", "User", "+1234567890");
    }

    @Test
    void goalProgressUsesIncomeMinusExpensesSinceStartDate() {
        SavingsGoal goal = new SavingsGoal(
                "Emergency Fund",
                BigDecimal.valueOf(5000),
                LocalDate.now().plusYears(1),
                LocalDate.of(2024, 1, 1),
                user);
        Category income = new Category("Salary", CategoryType.INCOME, false, null);
        Category expense = new Category("Food", CategoryType.EXPENSE, false, null);
        when(transactionRepository.findFromDate(user, LocalDate.of(2024, 1, 1))).thenReturn(List.of(
                new Transaction(BigDecimal.valueOf(1500), LocalDate.of(2024, 1, 5), income, "pay", user),
                new Transaction(BigDecimal.valueOf(500), LocalDate.of(2024, 1, 6), expense, "food", user)));

        GoalResponse response = service.toResponse(goal);

        assertThat(response.currentProgress()).isEqualTo("1000.00");
        assertThat(response.progressPercentage()).isEqualTo("20.0");
        assertThat(response.remainingAmount()).isEqualTo("4000.00");
    }
}
