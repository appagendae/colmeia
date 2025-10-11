package com.agendae.colmeia.domain.port.in;

import java.util.UUID;

public interface SyncCalendar {
    /**
     * Gera a URL de autorização do Google.
     * @param state Um valor aleatório para proteção contra CSRF.
     * @return A URL para onde o utilizador deve ser redirecionado.
     */
    String getGoogleAuthorizationUrl(String state);

    /**
     * Lida com o callback do Google após a autorização.
     * @param state O valor de estado retornado pelo Google (para validação).
     * @param code O código de autorização a ser trocado por tokens.
     * @return O nome do utilizador autenticado para ser exibido no frontend.
     */
    String handleGoogleCallback(String state, String code);

    /**
     * Sincroniza o calendário para uma conta externa específica.
     * @param externalAccountId O ID da conta a ser sincronizada.
     */
    void syncCalendarForAccount(UUID externalAccountId);
}

