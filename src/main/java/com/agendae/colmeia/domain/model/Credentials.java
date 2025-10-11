package com.agendae.colmeia.domain.model;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Representa as credenciais de autenticação de uma conta externa.
 * Este é um objeto de domínio puro, não acoplado a nenhuma tecnologia de persistência.
 */
@Getter
@Setter
@Builder
public class Credentials {

    private UUID id;
    private UUID externalAccountId;

    /**
     * O token de acesso principal, usado para fazer chamadas à API.
     * Na vida real, este campo conteria o JSON completo e criptografado.
     */
    private String accessToken;

    /**
     * O token usado para obter um novo accessToken quando o atual expira.
     */
    private String refreshToken;

    /**
     * O momento em que o accessToken expira.
     */
    private Instant expiresAt;

    /**
     * Os escopos (permissões) que o usuário concedeu.
     */
    private List<String> scopes;
}
