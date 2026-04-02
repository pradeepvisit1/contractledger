package com.contractledger.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "wage_log_entries")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WageLogEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wage_log_id", nullable = false)
    private WageLog wageLog;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "worker_id", nullable = false)
    private Worker worker;

    @Column(precision = 10, scale = 2)
    private BigDecimal amount;
}
