package com.example.finance.service;

import com.example.finance.dto.GoalDtos.GoalRequest;
import com.example.finance.dto.GoalDtos.GoalResponse;
import com.example.finance.dto.GoalDtos.GoalUpdateRequest;
import com.example.finance.entity.CategoryType;
import com.example.finance.entity.SavingsGoal;
import com.example.finance.entity.Transaction;
import com.example.finance.entity.User;
import com.example.finance.exception.ApiException;
import com.example.finance.repository.SavingsGoalRepository;
import com.example.finance.repository.TransactionRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GoalService {
    private final SavingsGoalRepository goalRepository;
    private final TransactionRepository transactionRepository;
    private final CurrentUserService currentUserService;

    public GoalService(
            SavingsGoalRepository goalRepository,
            TransactionRepository transactionRepository,
            CurrentUserService currentUserService) {
        this.goalRepository = goalRepository;
        this.transactionRepository = transactionRepository;
        this.currentUserService = currentUserService;
    }

    @Transactional
    public GoalResponse create(GoalRequest request) {
        User user = currentUserService.requireCurrentUser();
        validateTargetDate(request.targetDate());
        LocalDate startDate = request.startDate() == null ? LocalDate.now() : request.startDate();
        SavingsGoal goal = new SavingsGoal(
                request.goalName().trim(),
                request.targetAmount(),
                request.targetDate(),
                startDate,
                user);
        return toResponse(goalRepository.save(goal));
    }

    public List<GoalResponse> list() {
        User user = currentUserService.requireCurrentUser();
        return goalRepository.findByUserOrderByIdAsc(user).stream().map(this::toResponse).toList();
    }

    public GoalResponse get(Long id) {
        return toResponse(requireOwned(id));
    }

    @Transactional
    public GoalResponse update(Long id, GoalUpdateRequest request) {
        SavingsGoal goal = requireOwned(id);
        if (request.targetAmount() != null) {
            goal.setTargetAmount(request.targetAmount());
        }
        if (request.targetDate() != null) {
            validateTargetDate(request.targetDate());
            goal.setTargetDate(request.targetDate());
        }
        return toResponse(goal);
    }

    @Transactional
    public void delete(Long id) {
        goalRepository.delete(requireOwned(id));
    }

    public GoalResponse toResponse(SavingsGoal goal) {
        BigDecimal progress = currentProgress(goal);
        BigDecimal percentage = progress
                .multiply(BigDecimal.valueOf(100))
                .divide(goal.getTargetAmount(), 2, RoundingMode.HALF_UP);
        BigDecimal remaining = goal.getTargetAmount().subtract(progress).max(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);
        return new GoalResponse(
                goal.getId(),
                goal.getGoalName(),
                goal.getTargetAmount(),
                goal.getTargetDate(),
                goal.getStartDate(),
                progress.setScale(2, RoundingMode.HALF_UP),
                percentage,
                remaining);
    }

    private BigDecimal currentProgress(SavingsGoal goal) {
        return transactionRepository.findFromDate(goal.getUser(), goal.getStartDate()).stream()
                .map(this::signedAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal signedAmount(Transaction transaction) {
        return transaction.getCategory().getType() == CategoryType.INCOME
                ? transaction.getAmount()
                : transaction.getAmount().negate();
    }

    private SavingsGoal requireOwned(Long id) {
        User user = currentUserService.requireCurrentUser();
        SavingsGoal goal = goalRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Goal not found"));
        if (!goal.getUser().getId().equals(user.getId())) {
            throw new ApiException(HttpStatus.FORBIDDEN, "You cannot access this goal");
        }
        return goal;
    }

    private void validateTargetDate(LocalDate targetDate) {
        if (!targetDate.isAfter(LocalDate.now())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Target date must be in the future");
        }
    }
}
