package com.agendae.colmeia.domain.port.out;

import com.agendae.colmeia.domain.model.Appointment;
import com.agendae.colmeia.domain.model.Credentials;
import java.util.List;

public interface CalendarProviderPort {
    String getAuthorizationUrl(String userId);
    Credentials exchangeCodeForCredentials(String code);
    List<Appointment> listEvents(Credentials credentials);
}
