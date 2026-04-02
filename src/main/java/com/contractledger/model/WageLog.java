package com.contractledger.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "wage_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class WageLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "logged_by")
    private User loggedBy;

    @Column(nullable = false)
    private LocalDate logDate;

    @Column(precision = 14, scale = 2)
    private BigDecimal totalAmount;

    @Column(length = 300)
    private String notes;

    @CreatedDate
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "wageLog", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<WageLogEntry> entries;
}
