package com.contractledger.controller;

import com.contractledger.dto.InvestorDto;
import com.contractledger.dto.ReportDto;
import com.contractledger.model.Expense;
import com.contractledger.model.Investor;
import com.contractledger.model.Project;
import com.contractledger.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/projects/{projectId}/report")
@RequiredArgsConstructor
public class ReportController {

    private final ProjectRepository projectRepository;
    private final InvestorRepository investorRepository;
    private final ExpenseRepository expenseRepository;
    private final WageLogRepository wageLogRepository;

    @GetMapping
    public ResponseEntity<?> getReport(@PathVariable Long projectId) {
        Project project = projectRepository.findById(projectId).orElse(null);
        if (project == null) return ResponseEntity.notFound().build();

        BigDecimal invested = investorRepository.totalByProject(projectId);
        BigDecimal expenses = expenseRepository.totalByProject(projectId);
        BigDecimal wages    = wageLogRepository.totalByProject(projectId);
        BigDecimal grand    = expenses.add(wages);

        BigDecimal material  = expenseRepository.totalByProjectAndCategory(projectId, Expense.Category.MATERIAL);
        BigDecimal labor     = expenseRepository.totalByProjectAndCategory(projectId, Expense.Category.LABOR).add(wages);
        BigDecimal machinery = expenseRepository.totalByProjectAndCategory(projectId, Expense.Category.MACHINERY);
        BigDecimal transport = expenseRepository.totalByProjectAndCategory(projectId, Expense.Category.TRANSPORT);
        BigDecimal other     = expenseRepository.totalByProjectAndCategory(projectId, Expense.Category.OTHER);

        // Investors with share percentage
        List<Investor> investorList = investorRepository.findByProjectIdOrderByCreatedAtDesc(projectId);
        List<InvestorDto.Response> investorResponses = investorList.stream().map(inv -> {
            double share = invested.compareTo(BigDecimal.ZERO) > 0
                    ? inv.getAmount()
                        .multiply(BigDecimal.valueOf(100))
                        .divide(invested, 2, RoundingMode.HALF_UP)
                        .doubleValue()
                    : 0.0;
            return InvestorDto.Response.builder()
                    .id(inv.getId())
                    .name(inv.getName())
                    .phone(inv.getPhone())
                    .amount(inv.getAmount())
                    .investmentDate(inv.getInvestmentDate())
                    .sharePercent(share)
                    .build();
        }).collect(Collectors.toList());

        // Monthly expense summary
        List<ReportDto.MonthlyData> monthly = expenseRepository.monthlyTotals(projectId)
                .stream()
                .map(row -> ReportDto.MonthlyData.builder()
                        .month(row[0].toString())
                        .amount(new BigDecimal(row[1].toString()))
                        .build())
                .collect(Collectors.toList());

        return ResponseEntity.ok(ReportDto.Response.builder()
                .projectName(project.getName())
                .projectType(project.getType() != null ? project.getType().name() : "")
                .projectLocation(project.getLocation())
                .totalInvested(invested)
                .totalExpenses(expenses)
                .totalWages(wages)
                .grandExpenses(grand)
                .netBalance(invested.subtract(grand))
                .materialCost(material)
                .laborCost(labor)
                .machineryCost(machinery)
                .transportCost(transport)
                .otherCost(other)
                .investors(investorResponses)
                .monthlySummary(monthly)
                .build());
    }
}
