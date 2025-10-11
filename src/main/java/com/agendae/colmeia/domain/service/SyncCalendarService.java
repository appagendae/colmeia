package com.agendae.colmeia.domain.service;

import com.agendae.colmeia.domain.port.in.SyncCalendar;
import com.agendae.colmeia.domain.port.out.CalendarProviderPort;
import com.agendae.colmeia.domain.port.out.ExternalAccountRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
// A anotação @RequiredArgsConstructor foi removida
public class SyncCalendarService implements SyncCalendar {

    private final ExternalAccountRepositoryPort externalAccountRepository;
    private final CalendarProviderPort googleCalendarProvider;

    /**
     * Construtor explícito para injeção de dependência.
     * O Spring Boot usará este construtor para injetar as implementações
     * das portas (os adaptadores) quando a aplicação iniciar.
     * Este código faz exatamente o que @RequiredArgsConstructor faria.
     */
    public SyncCalendarService(
            ExternalAccountRepositoryPort externalAccountRepository,
            CalendarProviderPort googleCalendarProvider
    ) {
        this.externalAccountRepository = externalAccountRepository;
        this.googleCalendarProvider = googleCalendarProvider;
    }

    @Override
    public String getGoogleAuthorizationUrl(UUID userId) {
        // Lógica para associar a URL a um estado e ao usuário
        return googleCalendarProvider.getAuthorizationUrl(userId.toString());
    }

    @Override
    public void handleGoogleCallback(UUID userId, String code) {
        // Lógica de negócio para tratar o callback
        var credentials = googleCalendarProvider.exchangeCodeForCredentials(code);
        // ... Buscar informações do usuário usando as credenciais
        // ... Criar/atualizar ExternalAccount e salvar as credenciais
        // externalAccountRepository.save(...)
    }

    @Override
    public void syncCalendarForAccount(UUID externalAccountId) {
        // Lógica para buscar credenciais, chamar o provedor e salvar os compromissos
        var account = externalAccountRepository.findById(externalAccountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        // var events = googleCalendarProvider.listEvents(account.getCredentials());
        // ... salvar os eventos no banco de dados através de outro repositório
    }
}

