package com.contractledger.repository;

import com.contractledger.model.WageLogEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WageLogEntryRepository extends JpaRepository<WageLogEntry, Long> {
}
