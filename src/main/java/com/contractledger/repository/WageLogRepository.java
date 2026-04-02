package com.contractledger.repository;

import com.contractledger.model.WageLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface WageLogRepository extends JpaRepository<WageLog, Long> {

    List<WageLog> findByProjectIdOrderByLogDateDesc(Long projectId);

    @Query("SELECT COALESCE(SUM(w.totalAmount), 0) FROM WageLog w WHERE w.project.id = :pid")
    BigDecimal totalByProject(@Param("pid") Long pid);
}
