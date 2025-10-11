package com.agendae.colmeia.infra.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "appointments")
@Getter
@Setter
public class AppointmentEntity {

    @Id
    private UUID id;

    @Column(name = "external_account_id", nullable = false)
    private UUID externalAccountId;

    @Column(name = "external_event_id", nullable = false)
    private String externalEventId;

    @Column(name = "title")
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "start_time")
    private OffsetDateTime startTime;

    @Column(name = "end_time")
    private OffsetDateTime endTime;

    @Column(name = "location")
    private String location;

    @Column(name = "status")
    private String status;

    @JdbcTypeCode(SqlTypes.JSON) // Mapeia o Map para um campo JSON no banco de dados (requer suporte do dialeto do Hibernate/BD)
    @Column(name = "raw_data", columnDefinition = "jsonb") // Use 'json' para MySQL
    private Map<String, Object> rawData;
}