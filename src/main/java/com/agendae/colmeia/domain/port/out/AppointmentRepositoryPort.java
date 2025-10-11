package com.agendae.colmeia.domain.port.out;

import com.agendae.colmeia.domain.model.Appointment;
import java.util.List;

public interface AppointmentRepositoryPort {
    /**
     * Salva uma lista de compromissos no banco de dados.
     * Se um compromisso já existir, ele será atualizado.
     *
     * @param appointments A lista de compromissos a ser salva.
     */
    void saveAll(List<Appointment> appointments);
}
