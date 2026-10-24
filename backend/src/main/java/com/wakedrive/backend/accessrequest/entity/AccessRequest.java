package com.wakedrive.backend.accessrequest.entity;

import com.wakedrive.backend.city.entity.City;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "access_requests")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccessRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "access_request_id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "company_name", length = 100, nullable = false)
    private String companyName;

    @Column(name = "nit", length = 12, nullable = false)
    private String nit;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "city_id", nullable = false)
    private City city;

    @Column(name = "address", length = 150, nullable = false)
    private String address;

    @Column(name = "company_phone", length = 10, nullable = false)
    private String companyPhone;

    @Column(name = "admin_name", length = 100, nullable = false)
    private String adminName;

    @Column(name = "admin_email", length = 100, nullable = false)
    private String adminEmail;

    @Column(name = "admin_phone", length = 10, nullable = false)
    private String adminPhone;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    @Builder.Default
    private AccessRequestStatus status = AccessRequestStatus.PENDING;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Quién cambió la solicitud por última vez (el SUPER_ADMIN al aprobar o rechazar). Nulo si solo la creó el formulario público.
    @LastModifiedBy
    @Column(name = "updated_by", length = 100)
    private String updatedBy;
}
