package com.contractledger.controller;

import com.contractledger.dto.WorkerDto;
import com.contractledger.model.Project;
import com.contractledger.model.Worker;
import com.contractledger.repository.ProjectRepository;
import com.contractledger.repository.WorkerRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/projects/{projectId}/workers")
@RequiredArgsConstructor
public class WorkerController {

    private final WorkerRepository workerRepository;
    private final ProjectRepository projectRepository;

    @GetMapping
    public ResponseEntity<?> getAll(@PathVariable Long projectId) {
        if (!projectRepository.existsById(projectId)) {
            return ResponseEntity.notFound().build();
        }

        List<WorkerDto.Response> workers = workerRepository
                .findByProjectIdOrderByCreatedAtDesc(projectId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(workers);
    }

    @PostMapping
    public ResponseEntity<?> add(@PathVariable Long projectId,
                                  @Valid @RequestBody WorkerDto.Request request) {
        Project project = projectRepository.findById(projectId).orElse(null);
        if (project == null) return ResponseEntity.notFound().build();

        Worker worker = Worker.builder()
                .project(project)
                .name(request.getName())
                .role(request.getRole())
                .phone(request.getPhone())
                .dailyWage(request.getDailyWage())
                .active(true)
                .build();

        Worker saved = workerRepository.save(worker);
        return ResponseEntity.ok(toResponse(saved));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long projectId, @PathVariable Long id) {
        if (!workerRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        workerRepository.deleteById(id);
        return ResponseEntity.ok("Worker removed");
    }

    private WorkerDto.Response toResponse(Worker worker) {
        return WorkerDto.Response.builder()
                .id(worker.getId())
                .name(worker.getName())
                .role(worker.getRole())
                .phone(worker.getPhone())
                .dailyWage(worker.getDailyWage())
                .active(worker.isActive())
                .createdAt(worker.getCreatedAt() != null
                        ? worker.getCreatedAt().toLocalDate().toString() : null)
                .build();
    }
}
