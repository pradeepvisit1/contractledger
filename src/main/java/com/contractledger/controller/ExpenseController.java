package com.contractledger.controller;

import com.contractledger.dto.ExpenseDto;
import com.contractledger.model.Expense;
import com.contractledger.model.Project;
import com.contractledger.model.User;
import com.contractledger.repository.ExpenseRepository;
import com.contractledger.repository.ProjectRepository;
import com.contractledger.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/projects/{projectId}/expenses")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseRepository expenseRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<?> getAll(@PathVariable Long projectId,
                                     @RequestParam(required = false) String category) {
        if (!projectRepository.existsById(projectId)) {
            return ResponseEntity.notFound().build();
        }

        List<Expense> expenses;
        if (category != null) {
            try {
                Expense.Category cat = Expense.Category.valueOf(category.toUpperCase());
                expenses = expenseRepository.findByProjectIdAndCategoryOrderByExpenseDateDesc(projectId, cat);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body("Invalid category: " + category);
            }
        } else {
            expenses = expenseRepository.findByProjectIdOrderByExpenseDateDesc(projectId);
        }

        return ResponseEntity.ok(expenses.stream().map(this::toResponse).collect(Collectors.toList()));
    }

    @PostMapping
    public ResponseEntity<?> add(@PathVariable Long projectId,
                                  @Valid @RequestBody ExpenseDto.Request request,
                                  Authentication authentication) {
        Project project = projectRepository.findById(projectId).orElse(null);
        if (project == null) return ResponseEntity.notFound().build();

        User currentUser = userRepository.findByUsername(authentication.getName()).orElse(null);

        Expense expense = Expense.builder()
                .project(project)
                .createdBy(currentUser)
                .description(request.getDescription())
                .amount(request.getAmount())
                .category(Expense.Category.valueOf(request.getCategory().toUpperCase()))
                .expenseDate(request.getExpenseDate())
                .paidTo(request.getPaidTo())
                .billNumber(request.getBillNumber())
                .notes(request.getNotes())
                .build();

        Expense saved = expenseRepository.save(expense);
        return ResponseEntity.ok(toResponse(saved));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long projectId, @PathVariable Long id) {
        if (!expenseRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        expenseRepository.deleteById(id);
        return ResponseEntity.ok("Expense deleted");
    }

    private ExpenseDto.Response toResponse(Expense expense) {
        return ExpenseDto.Response.builder()
                .id(expense.getId())
                .description(expense.getDescription())
                .amount(expense.getAmount())
                .category(expense.getCategory().name())
                .expenseDate(expense.getExpenseDate())
                .paidTo(expense.getPaidTo())
                .billNumber(expense.getBillNumber())
                .notes(expense.getNotes())
                .createdBy(expense.getCreatedBy() != null ? expense.getCreatedBy().getFullName() : "")
                .createdAt(expense.getCreatedAt() != null
                        ? expense.getCreatedAt().toLocalDate().toString() : null)
                .build();
    }
}
