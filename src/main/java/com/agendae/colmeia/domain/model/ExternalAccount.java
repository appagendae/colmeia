package com.agendae.colmeia.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Representa a vinculação de uma conta de um provedor externo (Google, Outlook)
 * a um usuário do nosso sistema.
 */
@Getter
@Setter
@Builder
public class ExternalAccount {

    private UUID id;
    private UUID userId;
    private String provider; // Ex: "GOOGLE", "OUTLOOK"
    private String providerUserId;
    private String accountEmail;
    private OffsetDateTime createdAt;
    private Credentials credentials;

}
