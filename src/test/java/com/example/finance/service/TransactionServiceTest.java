package com.example.finance.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.finance.dto.TransactionDtos.TransactionRequest;
import com.example.finance.dto.TransactionDtos.TransactionResponse;
import com.example.finance.entity.Category;
import com.example.finance.entity.CategoryType;
import com.example.finance.entity.Transaction;
import com.example.finance.entity.User;
import com.example.finance.exception.ApiException;
import com.example.finance.repository.TransactionRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {
    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private CategoryService categoryService;
    @Mock
    private CurrentUserService currentUserService;

    private TransactionService service;
    private User user;
    private Category salary;

    @BeforeEach
    void setUp() {
        service = new TransactionService(transactionRepository, categoryService, currentUserService);
        user = new User("user@example.com", "password", "User", "+1234567890");
        salary = new Category("Salary", CategoryType.INCOME, false, null);
    }

    @Test
    void createRejectsFutureTransactionDate() {
        when(currentUserService.requireCurrentUser()).thenReturn(user);
        TransactionRequest request = new TransactionRequest(
                BigDecimal.TEN,
                LocalDate.now().plusDays(1),
                "Salary",
                "future");

        assertThatThrownBy(() -> service.create(request))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("future");
    }

    @Test
    void createPersistsTransactionWithAccessibleCategory() {
        when(currentUserService.requireCurrentUser()).thenReturn(user);
        when(categoryService.requireAccessibleByName(user, "Salary")).thenReturn(salary);
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TransactionResponse response = service.create(new TransactionRequest(
                BigDecimal.valueOf(5000),
                LocalDate.now(),
                "Salary",
                "pay"));

        assertThat(response.category()).isEqualTo("Salary");
        assertThat(response.type()).isEqualTo(CategoryType.INCOME);

        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository).save(captor.capture());
        assertThat(captor.getValue().getAmount()).isEqualByComparingTo("5000");
    }
}
