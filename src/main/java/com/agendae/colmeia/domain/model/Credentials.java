package com.agendae.colmeia.domain.model;

// Anotações do Lombok removidas
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Representa as credenciais de autenticação de uma conta externa.
 * Este é um objeto de domínio puro, não acoplado a nenhuma tecnologia de persistência.
 */
public class Credentials {

    private UUID id;
    private UUID externalAccountId;
    private String accessToken;
    private String refreshToken;
    private Instant expiresAt;
    private List<String> scopes;

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

    // Getters e Setters manuais
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getExternalAccountId() { return externalAccountId; }
    public void setExternalAccountId(UUID externalAccountId) { this.externalAccountId = externalAccountId; }

    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }

    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }

    public Instant getExpiresAt() { return expiresAt; }
    public void setExpiresAt(Instant expiresAt) { this.expiresAt = expiresAt; }

    public List<String> getScopes() { return scopes; }
    public void setScopes(List<String> scopes) { this.scopes = scopes; }
}

