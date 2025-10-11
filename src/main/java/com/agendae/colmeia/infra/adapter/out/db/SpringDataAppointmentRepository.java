package com.agendae.colmeia.infra.adapter.out.db;

import com.agendae.colmeia.infra.entity.AppointmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SpringDataAppointmentRepository extends JpaRepository<AppointmentEntity, UUID> {
    // Pode adicionar métodos de consulta personalizados aqui se necessário
}