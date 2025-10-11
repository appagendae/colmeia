package com.agendae.colmeia.domain.model;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Representa um compromisso ou evento da agenda.
 * Este é um objeto de domínio puro.
 */
@Getter
@Setter
@Builder
public class Appointment {

    private UUID id;
    private UUID externalAccountId;
    private String externalEventId;
    private String title;
    private String description;
    private OffsetDateTime startTime;
    private OffsetDateTime endTime;
    private String location;
    private String status;

    /**
     * Campo para armazenar o payload original do evento da API externa (ex: Google Calendar).
     * Útil para análises futuras e para evitar perda de dados específicos do provedor.
     */
    private Map<String, Object> rawData;
}
