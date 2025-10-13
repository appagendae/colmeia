package com.agendae.colmeia.domain.service;

import com.agendae.colmeia.domain.model.Appointment;
import com.agendae.colmeia.domain.model.Credentials;
import com.agendae.colmeia.domain.model.ExternalAccount;
import com.agendae.colmeia.domain.port.in.SyncCalendar;
import com.agendae.colmeia.domain.port.out.AppointmentRepositoryPort;
import com.agendae.colmeia.domain.port.out.CalendarProviderPort;
import com.agendae.colmeia.domain.port.out.ExternalAccountRepositoryPort;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.oauth2.Oauth2;
import com.google.api.services.oauth2.model.Userinfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class SyncCalendarService implements SyncCalendar {

    private final ExternalAccountRepositoryPort externalAccountRepository;
    private final AppointmentRepositoryPort appointmentRepository;
    private final CalendarProviderPort googleCalendarProvider;
    private final GoogleAuthorizationCodeFlow flow;

    @Value("${google.redirect-uri}")
    private String redirectUri;

    public SyncCalendarService(
            ExternalAccountRepositoryPort externalAccountRepository,
            AppointmentRepositoryPort appointmentRepository,
            CalendarProviderPort googleCalendarProvider,
            GoogleAuthorizationCodeFlow flow
    ) {
        this.externalAccountRepository = externalAccountRepository;
        this.appointmentRepository = appointmentRepository;
        this.googleCalendarProvider = googleCalendarProvider;
        this.flow = flow;
    }

    @Override
    public String getGoogleAuthorizationUrl(String state) {
        return googleCalendarProvider.getAuthorizationUrl(state);
    }

    @Override
    @Transactional
    public String handleGoogleCallback(String state, String code) {
        try {
            // 1. Usa o 'flow' para trocar o código por um token de resposta
            GoogleTokenResponse tokenResponse = flow.newTokenRequest(code)
                    .setRedirectUri(redirectUri)
                    .execute();
            //TODO refatorar para usar a google-auth-library-oauth2-http
            // 2. Cria uma credencial TEMPORÁRIA para descobrir quem é o usuário
            Credential credential = new GoogleCredential.Builder()
                    .setTransport(flow.getTransport())
                    .setJsonFactory(flow.getJsonFactory())
                    .build()
                    .setFromTokenResponse(tokenResponse);

            // 3. Usa a credencial temporária para obter as informações do perfil
            Userinfo userInfo = getUserInfoFromGoogle(credential);
            String userEmail = userInfo.getEmail();
            String userName = userInfo.getName();
            String googleUserId = userInfo.getId();

            // 4. AGORA, armazena a credencial permanentemente usando o ID de usuário correto
            flow.createAndStoreCredential(tokenResponse, googleUserId);

            // 5. Procura ou cria a conta externa no seu banco de dados
            ExternalAccount account = externalAccountRepository.findByProviderAndProviderUserId("GOOGLE", googleUserId)
                    .orElseGet(() -> {
                        ExternalAccount newAccount = new ExternalAccount();
                        newAccount.setProvider("GOOGLE");
                        newAccount.setProviderUserId(googleUserId);
                        newAccount.setAccountEmail(userEmail);
                        newAccount.setCreatedAt(OffsetDateTime.now());
                        return externalAccountRepository.save(newAccount);
                    });

            // 6. Converte o objeto Credential oficial para o seu modelo de domínio
            Credentials credentials = new Credentials();
            credentials.setAccessToken(credential.getAccessToken());
            credentials.setRefreshToken(credential.getRefreshToken());
            credentials.setExpiresIn(credential.getExpiresInSeconds());
            credentials.setExternalAccountId(account.getId());
            // TODO: A lógica para salvar seu objeto 'Credentials' no banco de dados deve ser implementada aqui

            account.setCredentials(credentials); // Associa para uso imediato

            syncCalendarForAccount(account.getId());

            return userName;

        } catch (IOException e) {
            throw new RuntimeException("Erro ao processar o callback do Google", e);
        }
    }

    @Override
    @Transactional
    public void syncCalendarForAccount(UUID externalAccountId) {
        ExternalAccount account = externalAccountRepository.findById(externalAccountId)
                .orElseThrow(() -> new RuntimeException("Conta externa não encontrada"));

        if (account.getCredentials() == null) {
            throw new IllegalStateException("Credenciais não encontradas para a conta. A lógica para buscar do DB precisa ser implementada.");
        }

        List<Appointment> events = googleCalendarProvider.listEvents(account.getCredentials());

        for (Appointment event : events) {
            event.setExternalAccountId(externalAccountId);
        }

        appointmentRepository.saveAll(events);
        System.out.println("Sincronizados " + events.size() + " eventos para a conta " + externalAccountId);
    }

    private Userinfo getUserInfoFromGoogle(Credential credential) {
        try {
            Oauth2 oauth2Service = new Oauth2.Builder(
                    new NetHttpTransport(),
                    GsonFactory.getDefaultInstance(),
                    credential)
                    .setApplicationName("Agendae Colmeia")
                    .build();

            return oauth2Service.userinfo().get().execute();
        } catch (IOException e) {
            throw new RuntimeException("Erro ao obter informações do perfil do Google", e);
        }
    }
}