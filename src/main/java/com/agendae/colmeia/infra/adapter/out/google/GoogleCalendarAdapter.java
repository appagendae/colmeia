package com.agendae.colmeia.infra.adapter.out.google;

import com.agendae.colmeia.domain.model.Appointment;
import com.agendae.colmeia.domain.model.Credentials;
import com.agendae.colmeia.domain.port.out.CalendarProviderPort;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.util.Collections;
import java.util.List;

@Component("googleCalendarProvider")
public class GoogleCalendarAdapter implements CalendarProviderPort {

    private final GoogleAuthorizationCodeFlow flow;
    private final String redirectUri;

    /**
     * Construtor para Injeção de Dependência.
     * O Spring irá injetar automaticamente o Bean 'googleAuthorizationCodeFlow'
     * que foi criado na sua classe GoogleApiConfig.
     * @param flow O fluxo de autorização do Google.
     * @param redirectUri A URL de redirecionamento, injetada a partir de application.properties.
     */
    public GoogleCalendarAdapter(GoogleAuthorizationCodeFlow flow, @Value("${google.redirect-uri}") String redirectUri) {
        this.flow = flow;
        this.redirectUri = redirectUri;
    }

    @Override
    public String getAuthorizationUrl(String userId) {
        // Usa o 'flow' injetado para construir a URL de autorização dinamicamente.
        return flow.newAuthorizationUrl()
                .setRedirectUri(redirectUri)
                .setState(userId) // O 'state' é usado para associar a resposta ao usuário.
                .build();
    }

    @Override
    public Credentials exchangeCodeForCredentials(String code) {
        try {
            // Usa o 'flow' para trocar o código de autorização por tokens de acesso.
            GoogleTokenResponse tokenResponse = flow.newTokenRequest(code)
                    .setRedirectUri(redirectUri)
                    .execute();

            // Mapeia a resposta do Google para o nosso modelo de domínio 'Credentials'.
            return new Credentials(
                    null, // id (será gerado pelo DB)
                    null, // externalAccountId (será associado no serviço)
                    tokenResponse.getAccessToken(),
                    tokenResponse.getRefreshToken(),
                    Instant.now().plusSeconds(tokenResponse.getExpiresInSeconds()),
                    List.of(tokenResponse.getScope().split(" "))
            );
        } catch (IOException e) {
            // TODO lançar uma exceção de domínio própria.
            throw new RuntimeException("Erro ao trocar o código pelo token", e);
        }
    }

    @Override
    public List<Appointment> listEvents(Credentials credentials) {
        // A lógica para listar eventos usaria as credenciais para construir o serviço do Calendar.
        // Esta parte será implementada futuramente.
        System.out.println("A listar eventos do Google Calendar...");
        return Collections.emptyList();
    }
}

