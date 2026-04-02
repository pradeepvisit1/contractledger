package com.contractledger.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "projects")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String name;

    @Enumerated(EnumType.STRING)
    private ProjectType type;

    @Column(length = 200)
    private String location;

    @Column(length = 500)
    private String description;

    private LocalDate startDate;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ProjectStatus status = ProjectStatus.ACTIVE;

    @CreatedDate
    private LocalDateTime createdAt;

    public enum ProjectType {
        CANAL, ROAD, BUILDING, BRIDGE, OTHER
    }

    public enum ProjectStatus {
        ACTIVE, COMPLETED, ON_HOLD
    }
}
