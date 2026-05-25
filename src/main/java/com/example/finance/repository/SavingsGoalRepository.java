package com.example.finance.repository;

import com.example.finance.entity.SavingsGoal;
import com.example.finance.entity.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SavingsGoalRepository extends JpaRepository<SavingsGoal, Long> {
    List<SavingsGoal> findByUserOrderByIdAsc(User user);
}
