package com.contractledger.repository;

import com.contractledger.model.Worker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkerRepository extends JpaRepository<Worker, Long> {

    List<Worker> findByProjectIdOrderByCreatedAtDesc(Long projectId);

    List<Worker> findByProjectIdAndActiveTrue(Long projectId);
}
