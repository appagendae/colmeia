package com.agendae.colmeia.domain.service;

import com.agendae.colmeia.domain.port.out.AppointmentRepositoryPort;
import com.agendae.colmeia.domain.port.out.CalendarProviderPort;
import com.agendae.colmeia.domain.port.out.ExternalAccountRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SyncCalendarServiceTest {

    @Mock
    private ExternalAccountRepositoryPort externalAccountRepository;
    @Mock
    private AppointmentRepositoryPort appointmentRepository;
    @Mock
    private CalendarProviderPort googleCalendarProvider;
    @InjectMocks
    private SyncCalendarService syncCalendarService;

    private String testState;

    @BeforeEach
    void setUp() {
        testState = UUID.randomUUID().toString();
    }

    @Test
    @DisplayName("Deve retornar a URL de autorização do Google corretamente")
    void getGoogleAuthorizationUrl_shouldReturnCorrectUrl() {
        // --- ARRANGE ---
        String fakeAuthUrl = "https://google.com/auth/url?state=" + testState;

        // O mock agora espera uma String
        when(googleCalendarProvider.getAuthorizationUrl(testState)).thenReturn(fakeAuthUrl);

        // --- ACT ---
        // A chamada ao serviço agora passa uma String
        String actualUrl = syncCalendarService.getGoogleAuthorizationUrl(testState);

        // --- ASSERT ---
        assertNotNull(actualUrl);
        assertEquals(fakeAuthUrl, actualUrl);
        verify(googleCalendarProvider, times(1)).getAuthorizationUrl(testState);
        verifyNoInteractions(externalAccountRepository, appointmentRepository);
    }

    @Test
    @DisplayName("Deve lançar uma exceção ao tentar sincronizar uma conta inexistente")
    void syncCalendarForAccount_whenAccountNotFound_shouldThrowException() {
        // --- ARRANGE ---
        UUID nonExistentAccountId = UUID.randomUUID();
        when(externalAccountRepository.findById(any(UUID.class))).thenReturn(java.util.Optional.empty());

        // --- ACT & ASSERT ---
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            syncCalendarService.syncCalendarForAccount(nonExistentAccountId);
        });

        assertEquals("Conta externa não encontrada", exception.getMessage());
        verify(externalAccountRepository, times(1)).findById(nonExistentAccountId);
        verifyNoInteractions(googleCalendarProvider, appointmentRepository);
    }
}

