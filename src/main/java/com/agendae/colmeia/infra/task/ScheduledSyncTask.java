package com.agendae.colmeia.infra.task;

import com.agendae.colmeia.domain.port.in.SyncCalendar;
import com.agendae.colmeia.domain.port.out.ExternalAccountRepositoryPort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledSyncTask {

    private final SyncCalendar syncCalendar;
    private final ExternalAccountRepositoryPort accountRepository;

    public ScheduledSyncTask(SyncCalendar syncCalendar, ExternalAccountRepositoryPort accountRepository) {
        this.syncCalendar = syncCalendar;
        this.accountRepository = accountRepository;
    }

    // Cron expression: "às 2 da manhã todos os dias"
    @Scheduled(cron = "0 0 2 * * ?")
    public void syncAllCalendars() {
        System.out.println("Iniciando tarefa agendada de sincronização de calendários...");
        // TODO buscar as contas em lotes (paginação)
        var allAccounts = accountRepository.findAll();
        for (var account : allAccounts) {
            try {
                System.out.println("Sincronizando conta: " + account.getId());
                syncCalendar.syncCalendarForAccount(account.getId());
            } catch (Exception e) {
                // Exibir o erro e continuar com a próxima conta
                System.err.println("Falha ao sincronizar conta " + account.getId() + ": " + e.getMessage());
            }
        }
        System.out.println("Tarefa agendada de sincronização concluída.");
    }
}
