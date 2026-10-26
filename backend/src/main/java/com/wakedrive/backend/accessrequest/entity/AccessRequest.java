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

@Entity
@Table(name = "access_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccessRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "access_request_id", updatable = false, nullable = false)
    private Integer id;

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
}
