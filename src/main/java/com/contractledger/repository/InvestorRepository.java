package com.contractledger.repository;

import com.contractledger.model.Investor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface InvestorRepository extends JpaRepository<Investor, Long> {

    List<Investor> findByProjectIdOrderByCreatedAtDesc(Long projectId);

    @Query("SELECT COALESCE(SUM(i.amount), 0) FROM Investor i WHERE i.project.id = :pid")
    BigDecimal totalByProject(@Param("pid") Long pid);
}
