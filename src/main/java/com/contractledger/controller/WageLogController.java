package com.contractledger.controller;

import com.contractledger.dto.WageLogDto;
import com.contractledger.model.*;
import com.contractledger.repository.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/projects/{projectId}/wagelogs")
@RequiredArgsConstructor
public class WageLogController {

    private final WageLogRepository wageLogRepository;
    private final WageLogEntryRepository wageLogEntryRepository;
    private final WorkerRepository workerRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<?> getAll(@PathVariable Long projectId) {
        if (!projectRepository.existsById(projectId)) {
            return ResponseEntity.notFound().build();
        }

        List<WageLogDto.Response> logs = wageLogRepository
                .findByProjectIdOrderByLogDateDesc(projectId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(logs);
    }

    @PostMapping
    public ResponseEntity<?> add(@PathVariable Long projectId,
                                  @Valid @RequestBody WageLogDto.Request request,
                                  Authentication authentication) {
        Project project = projectRepository.findById(projectId).orElse(null);
        if (project == null) return ResponseEntity.notFound().build();

        List<Worker> workers = workerRepository.findAllById(request.getWorkerIds());
        if (workers.isEmpty()) {
            return ResponseEntity.badRequest().body("No valid workers found");
        }

        User currentUser = userRepository.findByUsername(authentication.getName()).orElse(null);

        BigDecimal totalAmount = workers.stream()
                .map(w -> w.getDailyWage() != null ? w.getDailyWage() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        WageLog wageLog = WageLog.builder()
                .project(project)
                .loggedBy(currentUser)
                .logDate(request.getLogDate())
                .totalAmount(totalAmount)
                .notes(request.getNotes())
                .build();

        wageLogRepository.save(wageLog);

        // Save individual entries for each worker
        for (Worker worker : workers) {
            WageLogEntry entry = WageLogEntry.builder()
                    .wageLog(wageLog)
                    .worker(worker)
                    .amount(worker.getDailyWage() != null ? worker.getDailyWage() : BigDecimal.ZERO)
                    .build();
            wageLogEntryRepository.save(entry);
        }

        return ResponseEntity.ok(toResponse(wageLog));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long projectId, @PathVariable Long id) {
        if (!wageLogRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        wageLogRepository.deleteById(id);
        return ResponseEntity.ok("Wage log deleted");
    }

    private WageLogDto.Response toResponse(WageLog log) {
        List<String> workerNames = log.getEntries() != null
                ? log.getEntries().stream()
                    .map(entry -> entry.getWorker().getName())
                    .collect(Collectors.toList())
                : List.of();

        return WageLogDto.Response.builder()
                .id(log.getId())
                .logDate(log.getLogDate())
                .totalAmount(log.getTotalAmount())
                .notes(log.getNotes())
                .loggedBy(log.getLoggedBy() != null ? log.getLoggedBy().getFullName() : "")
                .workerNames(workerNames)
                .workerCount(workerNames.size())
                .createdAt(log.getCreatedAt() != null
                        ? log.getCreatedAt().toLocalDate().toString() : null)
                .build();
    }
}
