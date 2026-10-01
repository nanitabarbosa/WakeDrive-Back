package com.wakedrive.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "company")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "nit", length = 30, nullable = false, unique = true)
    private String nit;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "address", length = 150)
    private String address;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "email")
    private String email;

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
}
