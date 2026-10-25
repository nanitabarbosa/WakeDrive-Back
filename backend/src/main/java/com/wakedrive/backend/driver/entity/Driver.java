package com.wakedrive.backend.driver.entity;

import com.wakedrive.backend.common.entity.RecordStatus;
import com.wakedrive.backend.company.entity.Company;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "drivers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Driver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "driver_id", updatable = false, nullable = false)
    private Integer id;

    @Column(name = "full_name", length = 100, nullable = false)
    private String fullName;

    @Column(name = "document", length = 10, nullable = false, unique = true)
    private String document;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "phone", length = 10)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    @Builder.Default
    private RecordStatus status = RecordStatus.ACTIVE;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
