package com.agendae.colmeia.domain.model;

// Anotações do Lombok removidas
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Representa as credenciais de autenticação de uma conta externa.
 * Este é um objeto de domínio puro, não acoplado a nenhuma tecnologia de persistência.
 */
@Setter
@Getter
public class Credentials {

    // Getters e Setters manuais
    private UUID id;
    private UUID externalAccountId;
    private String accessToken;
    private String refreshToken;
    private Instant expiresAt;
    private List<String> scopes;
    private Long expiresIn;

    // Construtor vazio
    public Credentials() {}

    // Construtor com todos os campos
    public Credentials(UUID id, UUID externalAccountId, String accessToken, String refreshToken, Instant expiresAt, List<String> scopes) {
        this.id = id;
        this.externalAccountId = externalAccountId;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiresAt = expiresAt;
        this.scopes = scopes;
    }

}

