package com.contractledger.repository;

import com.contractledger.model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    List<Expense> findByProjectIdOrderByExpenseDateDesc(Long projectId);

    List<Expense> findByProjectIdAndCategoryOrderByExpenseDateDesc(Long projectId, Expense.Category category);

    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e WHERE e.project.id = :pid")
    BigDecimal totalByProject(@Param("pid") Long pid);

    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e WHERE e.project.id = :pid AND e.category = :cat")
    BigDecimal totalByProjectAndCategory(@Param("pid") Long pid, @Param("cat") Expense.Category cat);

    @Query("SELECT FUNCTION('DATE_FORMAT', e.expenseDate, '%Y-%m') AS mo, COALESCE(SUM(e.amount), 0) AS tot " +
           "FROM Expense e WHERE e.project.id = :pid GROUP BY mo ORDER BY mo DESC")
    List<Object[]> monthlyTotals(@Param("pid") Long pid);
}
