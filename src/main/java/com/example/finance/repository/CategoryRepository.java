package com.example.finance.repository;

import com.example.finance.entity.Category;
import com.example.finance.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByUserIsNullOrUserOrderByCustomAscNameAsc(User user);

    boolean existsByCustomFalseAndNameIgnoreCase(String name);

    boolean existsByUserAndNameIgnoreCase(User user, String name);

    Optional<Category> findByUserAndNameIgnoreCase(User user, String name);

    Optional<Category> findByCustomFalseAndNameIgnoreCase(String name);

    @Query("""
            select c from Category c
            where lower(c.name) = lower(:name)
              and (c.user = :user or c.user is null)
            order by c.custom desc
            """)
    List<Category> findAccessibleByName(@Param("user") User user, @Param("name") String name);
}
