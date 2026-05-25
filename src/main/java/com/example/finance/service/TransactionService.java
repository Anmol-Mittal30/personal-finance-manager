package com.example.finance.service;

import com.example.finance.dto.TransactionDtos.TransactionRequest;
import com.example.finance.dto.TransactionDtos.TransactionResponse;
import com.example.finance.dto.TransactionDtos.TransactionUpdateRequest;
import com.example.finance.entity.Category;
import com.example.finance.entity.CategoryType;
import com.example.finance.entity.Transaction;
import com.example.finance.entity.User;
import com.example.finance.exception.ApiException;
import com.example.finance.repository.TransactionRepository;
import java.time.LocalDate;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final CategoryService categoryService;
    private final CurrentUserService currentUserService;

    public TransactionService(
            TransactionRepository transactionRepository,
            CategoryService categoryService,
            CurrentUserService currentUserService) {
        this.transactionRepository = transactionRepository;
        this.categoryService = categoryService;
        this.currentUserService = currentUserService;
    }

    @Transactional
    public TransactionResponse create(TransactionRequest request) {
        User user = currentUserService.requireCurrentUser();
        if (request.date().isAfter(LocalDate.now())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Transaction date cannot be in the future");
        }
        Category category = categoryService.requireAccessibleByName(user, request.category());
        Transaction transaction = new Transaction(
                request.amount(),
                request.date(),
                category,
                request.description(),
                user);
        return toResponse(transactionRepository.save(transaction));
    }

    public List<TransactionResponse> list(LocalDate startDate, LocalDate endDate, Long categoryId, CategoryType type) {
        User user = currentUserService.requireCurrentUser();
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "startDate cannot be after endDate");
        }
        return transactionRepository.search(user, startDate, endDate, categoryId, type)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public TransactionResponse update(Long id, TransactionUpdateRequest request) {
        User user = currentUserService.requireCurrentUser();
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Transaction not found"));
        if (!transaction.getUser().getId().equals(user.getId())) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Transaction not found");
        }
        if (request.date() != null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Transaction date cannot be updated");
        }
        if (request.amount() != null) {
            transaction.setAmount(request.amount());
        }
        if (request.category() != null && !request.category().isBlank()) {
            transaction.setCategory(categoryService.requireAccessibleByName(user, request.category()));
        }
        if (request.description() != null) {
            transaction.setDescription(request.description());
        }
        return toResponse(transaction);
    }

    @Transactional
    public void delete(Long id) {
        User user = currentUserService.requireCurrentUser();
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Transaction not found"));
        if (!transaction.getUser().getId().equals(user.getId())) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Transaction not found");
        }
        transactionRepository.delete(transaction);
    }

    public TransactionResponse toResponse(Transaction transaction) {
        return new TransactionResponse(
                transaction.getId(),
                transaction.getAmount(),
                transaction.getDate(),
                transaction.getCategory().getName(),
                transaction.getDescription(),
                transaction.getCategory().getType());
    }
}
