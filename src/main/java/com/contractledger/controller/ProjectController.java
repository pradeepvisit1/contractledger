package com.contractledger.controller;

import com.contractledger.dto.ProjectDto;
import com.contractledger.model.Project;
import com.contractledger.repository.ExpenseRepository;
import com.contractledger.repository.InvestorRepository;
import com.contractledger.repository.ProjectRepository;
import com.contractledger.repository.WageLogRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectRepository projectRepository;
    private final InvestorRepository investorRepository;
    private final ExpenseRepository expenseRepository;
    private final WageLogRepository wageLogRepository;

    @GetMapping
    public List<ProjectDto.Response> getAll() {
        return projectRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectDto.Response> getById(@PathVariable Long id) {
        return projectRepository.findById(id)
                .map(p -> ResponseEntity.ok(toResponse(p)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ProjectDto.Response> create(@Valid @RequestBody ProjectDto.Request request) {
        Project project = Project.builder()
                .name(request.getName())
                .type(parseType(request.getType()))
                .location(request.getLocation())
                .description(request.getDescription())
                .startDate(request.getStartDate())
                .status(Project.ProjectStatus.ACTIVE)
                .build();

        return ResponseEntity.ok(toResponse(projectRepository.save(project)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProjectDto.Response> update(@PathVariable Long id,
                                                       @RequestBody ProjectDto.Request request) {
        return projectRepository.findById(id).map(project -> {
            if (request.getName() != null) project.setName(request.getName());
            if (request.getLocation() != null) project.setLocation(request.getLocation());
            if (request.getDescription() != null) project.setDescription(request.getDescription());
            if (request.getStartDate() != null) project.setStartDate(request.getStartDate());
            return ResponseEntity.ok(toResponse(projectRepository.save(project)));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        if (!projectRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        projectRepository.deleteById(id);
        return ResponseEntity.ok("Project deleted");
    }

    private ProjectDto.Response toResponse(Project project) {
        BigDecimal invested  = investorRepository.totalByProject(project.getId());
        BigDecimal expenses  = expenseRepository.totalByProject(project.getId());
        BigDecimal wages     = wageLogRepository.totalByProject(project.getId());
        BigDecimal grand     = expenses.add(wages);

        return ProjectDto.Response.builder()
                .id(project.getId())
                .name(project.getName())
                .type(project.getType() != null ? project.getType().name() : "OTHER")
                .location(project.getLocation())
                .description(project.getDescription())
                .status(project.getStatus().name())
                .startDate(project.getStartDate())
                .createdAt(project.getCreatedAt() != null
                        ? project.getCreatedAt().toLocalDate().toString() : null)
                .totalInvested(invested)
                .totalExpenses(expenses)
                .totalWages(wages)
                .grandExpenses(grand)
                .balance(invested.subtract(grand))
                .build();
    }

    private Project.ProjectType parseType(String type) {
        if (type == null) return Project.ProjectType.OTHER;
        try {
            return Project.ProjectType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            return Project.ProjectType.OTHER;
        }
    }
}
