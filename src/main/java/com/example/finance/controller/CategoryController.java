package com.example.finance.controller;

import com.example.finance.dto.AuthDtos.MessageResponse;
import com.example.finance.dto.CategoryDtos.CategoryRequest;
import com.example.finance.dto.CategoryDtos.CategoryResponse;
import com.example.finance.service.CategoryService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public Map<String, List<CategoryResponse>> list() {
        return Map.of("categories", categoryService.list());
    }

    @PostMapping
    public ResponseEntity<CategoryResponse> create(@Valid @RequestBody CategoryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.create(request));
    }

    @DeleteMapping("/{name}")
    public MessageResponse delete(@PathVariable String name) {
        categoryService.delete(name);
        return new MessageResponse("Category deleted successfully");
    }
}
