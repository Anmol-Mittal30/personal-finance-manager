package com.example.finance.repository;

import com.example.finance.entity.Transaction;
import com.example.finance.entity.User;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByUserOrderByDateDescIdDesc(User user);

    boolean existsByCategoryId(Long categoryId);

    @Query("""
            select t from Transaction t
            where t.user = :user
              and (:startDate is null or t.date >= :startDate)
              and (:endDate is null or t.date <= :endDate)
              and (:categoryId is null or t.category.id = :categoryId)
              and (:type is null or t.category.type = :type)
            order by t.date desc, t.id desc
            """)
    List<Transaction> search(
            @Param("user") User user,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("categoryId") Long categoryId,
            @Param("type") com.example.finance.entity.CategoryType type);

    @Query("""
            select t from Transaction t
            where t.user = :user and t.date >= :startDate
            """)
    List<Transaction> findFromDate(@Param("user") User user, @Param("startDate") LocalDate startDate);

    @Query("""
            select t from Transaction t
            where t.user = :user and year(t.date) = :year and month(t.date) = :month
            """)
    List<Transaction> findByMonth(@Param("user") User user, @Param("year") int year, @Param("month") int month);

    @Query("""
            select t from Transaction t
            where t.user = :user and year(t.date) = :year
            """)
    List<Transaction> findByYear(@Param("user") User user, @Param("year") int year);
}
