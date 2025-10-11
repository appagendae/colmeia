package com.agendae.colmeia.domain.port.in;

import java.util.UUID;

public interface SyncCalendar {
    String getGoogleAuthorizationUrl(UUID userId);
    void handleGoogleCallback(UUID userId, String code);
    void syncCalendarForAccount(UUID externalAccountId);
}
