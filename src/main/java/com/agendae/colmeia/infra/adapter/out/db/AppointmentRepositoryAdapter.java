package com.agendae.colmeia.infra.adapter.out.db;

import com.agendae.colmeia.domain.model.Appointment;
import com.agendae.colmeia.domain.port.out.AppointmentRepositoryPort;
import com.agendae.colmeia.infra.entity.AppointmentEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class AppointmentRepositoryAdapter implements AppointmentRepositoryPort {

    private final SpringDataAppointmentRepository jpaRepository;

    public AppointmentRepositoryAdapter(SpringDataAppointmentRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public void saveAll(List<Appointment> appointments) {
        List<AppointmentEntity> entities = appointments.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
        jpaRepository.saveAll(entities);
    }

    private AppointmentEntity toEntity(Appointment domainModel) {
        if (domainModel == null) return null;

        AppointmentEntity entity = new AppointmentEntity();
        entity.setId(domainModel.getId() != null ? domainModel.getId() : UUID.randomUUID());
        entity.setExternalAccountId(domainModel.getExternalAccountId());
        entity.setExternalEventId(domainModel.getExternalEventId());
        entity.setTitle(domainModel.getTitle());
        entity.setDescription(domainModel.getDescription());
        entity.setStartTime(domainModel.getStartTime());
        entity.setEndTime(domainModel.getEndTime());
        entity.setLocation(domainModel.getLocation());
        entity.setStatus(domainModel.getStatus());
        entity.setRawData(domainModel.getRawData());
        return entity;
    }

    private Appointment toDomainModel(AppointmentEntity entity) {
        if (entity == null) return null;

        return Appointment.builder()
                .id(entity.getId())
                .externalAccountId(entity.getExternalAccountId())
                .externalEventId(entity.getExternalEventId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .startTime(entity.getStartTime())
                .endTime(entity.getEndTime())
                .location(entity.getLocation())
                .status(entity.getStatus())
                .rawData(entity.getRawData())
                .build();
    }
}