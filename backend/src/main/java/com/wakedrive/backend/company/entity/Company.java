package com.wakedrive.backend.company.entity;

import com.wakedrive.backend.city.entity.City;
import com.wakedrive.backend.common.entity.RecordStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "companies")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "company_id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "nit", length = 12, nullable = false, unique = true)
    private String nit;

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Column(name = "address", length = 150, nullable = false)
    private String address;

    @Column(name = "phone", length = 10, nullable = false)
    private String phone;

    @Column(name = "email", length = 100, nullable = false)
    private String email;

    // Nullable: las empresas anteriores a este campo se rellenan desde su solicitud de acceso (ver sql/00_esquema.sql).
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_id")
    private City city;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private RecordStatus status = RecordStatus.ACTIVE;

    @Column(name = "face_recognition_always", nullable = false)
    @Builder.Default
    private Boolean faceRecognitionAlways = false;

    @Column(name = "notify_device_shutdown", nullable = false)
    @Builder.Default
    private Boolean notifyDeviceShutdown = false;

    @Column(name = "alarm_duration_seconds", nullable = false)
    @Builder.Default
    private Integer alarmDurationSeconds = 5;

    @Column(name = "alarm_sound_path")
    private String alarmSoundPath;

    @Column(name = "alarm_sound_name")
    private String alarmSoundName;

    @Column(name = "alarm_sound_size")
    private Long alarmSoundSize;

    @Column(name = "logo_path")
    private String logoPath;

    @Column(name = "logo_name")
    private String logoName;

    @Column(name = "logo_size")
    private Long logoSize;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @CreatedBy
    @Column(name = "created_by", length = 100, updatable = false)
    private String createdBy;

    @LastModifiedBy
    @Column(name = "updated_by", length = 100)
    private String updatedBy;
}
