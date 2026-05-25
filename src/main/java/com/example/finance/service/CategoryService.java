package com.example.finance.service;

import com.example.finance.dto.CategoryDtos.CategoryRequest;
import com.example.finance.dto.CategoryDtos.CategoryResponse;
import com.example.finance.entity.Category;
import com.example.finance.entity.User;
import com.example.finance.exception.ApiException;
import com.example.finance.repository.CategoryRepository;
import com.example.finance.repository.TransactionRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;
    private final CurrentUserService currentUserService;

    public CategoryService(
            CategoryRepository categoryRepository,
            TransactionRepository transactionRepository,
            CurrentUserService currentUserService) {
        this.categoryRepository = categoryRepository;
        this.transactionRepository = transactionRepository;
        this.currentUserService = currentUserService;
    }

    public List<CategoryResponse> list() {
        User user = currentUserService.requireCurrentUser();
        return categoryRepository.findByUserIsNullOrUserOrderByCustomAscNameAsc(user)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public CategoryResponse create(CategoryRequest request) {
        User user = currentUserService.requireCurrentUser();
        String name = request.name().trim();
        if (categoryRepository.existsByCustomFalseAndNameIgnoreCase(name)
                || categoryRepository.existsByUserAndNameIgnoreCase(user, name)) {
            throw new ApiException(HttpStatus.CONFLICT, "Category name already exists");
        }
        return toResponse(categoryRepository.save(new Category(name, request.type(), true, user)));
    }

    @Transactional
    public void delete(String name) {
        User user = currentUserService.requireCurrentUser();
        if (categoryRepository.findByCustomFalseAndNameIgnoreCase(name).isPresent()) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Default categories cannot be deleted");
        }
        Category category = categoryRepository.findByUserAndNameIgnoreCase(user, name)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Category not found"));

        if (transactionRepository.existsByCategoryId(category.getId())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Category is used by transactions");
        }
        categoryRepository.delete(category);
    }

    public Category requireAccessibleByName(User user, String name) {
        return categoryRepository.findAccessibleByName(user, name).stream()
                .findFirst()
                .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "Invalid category"));
    }

    private CategoryResponse toResponse(Category category) {
        return new CategoryResponse(category.getId(), category.getName(), category.getType(), category.isCustom());
    }
}
