package com.agendae.colmeia.domain.service;

import com.agendae.colmeia.domain.model.Appointment;
import com.agendae.colmeia.domain.model.Credentials;
import com.agendae.colmeia.domain.model.ExternalAccount;
import com.agendae.colmeia.domain.port.in.SyncCalendar;
import com.agendae.colmeia.domain.port.out.AppointmentRepositoryPort;
import com.agendae.colmeia.domain.port.out.CalendarProviderPort;
import com.agendae.colmeia.domain.port.out.ExternalAccountRepositoryPort;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.oauth2.Oauth2;
import com.google.api.services.oauth2.model.Userinfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class SyncCalendarService implements SyncCalendar {

    private final ExternalAccountRepositoryPort externalAccountRepository;
    private final AppointmentRepositoryPort appointmentRepository;
    private final CalendarProviderPort googleCalendarProvider;

    public SyncCalendarService(
            ExternalAccountRepositoryPort externalAccountRepository,
            AppointmentRepositoryPort appointmentRepository,
            CalendarProviderPort googleCalendarProvider
    ) {
        this.externalAccountRepository = externalAccountRepository;
        this.appointmentRepository = appointmentRepository;
        this.googleCalendarProvider = googleCalendarProvider;
    }

    @Override
    public String getGoogleAuthorizationUrl(String state) {
        return googleCalendarProvider.getAuthorizationUrl(state);
    }

    @Override
    @Transactional
    public String handleGoogleCallback(String state, String code) {
        // 1. Troca o código pelas credenciais (‘tokens’)
        Credentials credentials = googleCalendarProvider.exchangeCodeForCredentials(code);

        // 2. Usa as credenciais para obter informações do perfil do utilizador
        Userinfo userInfo = getUserInfoFromGoogle(credentials);
        String userEmail = userInfo.getEmail();
        String userName = userInfo.getName();
        String googleUserId = userInfo.getId();

        // 3. Procura ou cria a conta externa no seu banco de dados
        // Nota: findByProviderAndProviderUserId precisa ser adicionado à sua porta/adaptador
        ExternalAccount account = externalAccountRepository.findByProviderAndProviderUserId("GOOGLE", googleUserId)
                .orElseGet(() -> {
                    ExternalAccount newAccount = new ExternalAccount();
                    newAccount.setProvider("GOOGLE");
                    newAccount.setProviderUserId(googleUserId);
                    newAccount.setAccountEmail(userEmail);
                    // Aqui você criaria/associaria a um utilizador interno (tabela 'users')
                    // newAccount.setUserId(...)
                    newAccount.setCreatedAt(OffsetDateTime.now());
                    return externalAccountRepository.save(newAccount);
                });

        // 4. Salva as credenciais associadas a esta conta
        credentials.setExternalAccountId(account.getId());
        // TODO credenciais devem ser salvas num repositório próprio.
        // repository.save(credentials);

        // Associa as credenciais à conta para a sincronização imediata
        account.setCredentials(credentials);

        // Inicia a primeira sincronização em segundo plano (opcional)
        syncCalendarForAccount(account.getId());

        // 5. Retorna o nome do utilizador para ser exibido no frontend
        return userName;
    }

    @Override
    @Transactional
    public void syncCalendarForAccount(UUID externalAccountId) {
        ExternalAccount account = externalAccountRepository.findById(externalAccountId)
                .orElseThrow(() -> new RuntimeException("Conta externa não encontrada"));

        // TODO buscar as credenciais do banco de dados em vez de esperar que elas estejam no objeto.
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

    private Userinfo getUserInfoFromGoogle(Credentials credentials) {
        try {
            Credential credential = new GoogleCredential.Builder()
                    .build()
                    .setAccessToken(credentials.getAccessToken())
                    .setRefreshToken(credentials.getRefreshToken());

            Oauth2 oauth2Service = new Oauth2.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    GsonFactory.getDefaultInstance(),
                    credential)
                    .setApplicationName("Agendae Colmeia")
                    .build();

            return oauth2Service.userinfo().get().execute();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao obter informações do perfil do Google", e);
        }
    }
}

