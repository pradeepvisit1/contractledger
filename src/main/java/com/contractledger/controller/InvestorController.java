package com.contractledger.controller;

import com.contractledger.dto.InvestorDto;
import com.contractledger.model.Investor;
import com.contractledger.model.Project;
import com.contractledger.repository.InvestorRepository;
import com.contractledger.repository.ProjectRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/projects/{projectId}/investors")
@RequiredArgsConstructor
public class InvestorController {

    private final InvestorRepository investorRepository;
    private final ProjectRepository projectRepository;

    @GetMapping
    public ResponseEntity<?> getAll(@PathVariable Long projectId) {
        if (!projectRepository.existsById(projectId)) {
            return ResponseEntity.notFound().build();
        }

        List<Investor> investors = investorRepository.findByProjectIdOrderByCreatedAtDesc(projectId);
        BigDecimal total = investorRepository.totalByProject(projectId);

        List<InvestorDto.Response> response = investors.stream().map(investor -> {
            double share = total.compareTo(BigDecimal.ZERO) > 0
                    ? investor.getAmount()
                        .multiply(BigDecimal.valueOf(100))
                        .divide(total, 2, RoundingMode.HALF_UP)
                        .doubleValue()
                    : 0.0;

            return InvestorDto.Response.builder()
                    .id(investor.getId())
                    .name(investor.getName())
                    .phone(investor.getPhone())
                    .amount(investor.getAmount())
                    .investmentDate(investor.getInvestmentDate())
                    .notes(investor.getNotes())
                    .createdAt(investor.getCreatedAt() != null
                            ? investor.getCreatedAt().toLocalDate().toString() : null)
                    .sharePercent(share)
                    .build();
        }).collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<?> add(@PathVariable Long projectId,
                                  @Valid @RequestBody InvestorDto.Request request) {
        Project project = projectRepository.findById(projectId).orElse(null);
        if (project == null) return ResponseEntity.notFound().build();

        Investor investor = Investor.builder()
                .project(project)
                .name(request.getName())
                .phone(request.getPhone())
                .amount(request.getAmount())
                .investmentDate(request.getInvestmentDate())
                .notes(request.getNotes())
                .build();

        investorRepository.save(investor);
        return ResponseEntity.ok("Investor added successfully");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long projectId, @PathVariable Long id) {
        if (!investorRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        investorRepository.deleteById(id);
        return ResponseEntity.ok("Investor deleted");
    }
}
