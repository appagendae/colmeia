package com.agendae.colmeia.domain.service;

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

/**
 * Teste de Unidade para a classe de serviço SyncCalendarService.
 *
 * @ExtendWith(MockitoExtension.class) ativa o Mockito para criar os mocks.
 */
@ExtendWith(MockitoExtension.class)
class SyncCalendarServiceTest {

    // @Mock cria um objeto falso (mock) para a interface da porta de saída.
    // Não usaremos a implementação real do GoogleCalendarAdapter ou do repositório.
    @Mock
    private ExternalAccountRepositoryPort externalAccountRepository;

    @Mock
    private CalendarProviderPort googleCalendarProvider;

    // @InjectMocks cria uma instância da classe que queremos testar (SyncCalendarService)
    // e injeta automaticamente os mocks criados acima nela.
    @InjectMocks
    private SyncCalendarService syncCalendarService;

    private UUID testUserId;

    @BeforeEach
    void setUp() {
        // Código que roda antes de cada teste
        testUserId = UUID.randomUUID();
    }

    @Test
    @DisplayName("Deve retornar a URL de autorização do Google corretamente")
    void getGoogleAuthorizationUrl_shouldReturnCorrectUrl() {
        // --- ARRANGE (Organizar) ---
        // Aqui, nós definimos o comportamento esperado dos nossos mocks.
        String fakeAuthUrl = "https://google.com/auth/url_para_o_usuario_" + testUserId;
        String userIdAsString = testUserId.toString();

        // "Quando o método getAuthorizationUrl do mock googleCalendarProvider for chamado
        // com o argumento userIdAsString, então retorne a URL falsa."
        when(googleCalendarProvider.getAuthorizationUrl(userIdAsString)).thenReturn(fakeAuthUrl);

        // --- ACT (Agir) ---
        // Chamamos o método que realmente queremos testar no nosso serviço.
        String actualUrl = syncCalendarService.getGoogleAuthorizationUrl(testUserId);

        // --- ASSERT (Verificar) ---
        // Verificamos se o resultado foi o esperado.
        assertNotNull(actualUrl, "A URL não pode ser nula.");
        assertEquals(fakeAuthUrl, actualUrl, "A URL retornada não é a mesma que o mock forneceu.");

        // Também verificamos se nosso serviço interagiu com o mock como esperado.
        // "Verifique se o método getAuthorizationUrl foi chamado exatamente uma vez
        // no mock googleCalendarProvider com o argumento userIdAsString."
        verify(googleCalendarProvider, times(1)).getAuthorizationUrl(userIdAsString);

        // Garante que nenhuma outra interação aconteceu com este mock
        verifyNoMoreInteractions(googleCalendarProvider);
        // Garante que o repositório nem foi tocado neste caso de uso
        verifyNoInteractions(externalAccountRepository);
    }

    @Test
    @DisplayName("Deve lançar uma exceção ao tentar sincronizar uma conta inexistente")
    void syncCalendarForAccount_whenAccountNotFound_shouldThrowException() {
        // --- ARRANGE ---
        UUID nonExistentAccountId = UUID.randomUUID();

        // "Quando o método findById do mock externalAccountRepository for chamado
        // com qualquer UUID, então retorne um Optional vazio (simulando que não encontrou)."
        when(externalAccountRepository.findById(any(UUID.class))).thenReturn(java.util.Optional.empty());

        // --- ACT & ASSERT ---
        // Verificamos se, ao chamar o método, uma exceção do tipo RuntimeException é lançada.
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            syncCalendarService.syncCalendarForAccount(nonExistentAccountId);
        });

        // Verificamos se a mensagem da exceção é a esperada.
        assertEquals("Account not found", exception.getMessage());

        // Verificamos que o serviço tentou buscar a conta.
        verify(externalAccountRepository, times(1)).findById(nonExistentAccountId);

        // Crucial: garantimos que, como a conta não foi encontrada,
        // o serviço NUNCA tentou chamar a API do Google.
        verifyNoInteractions(googleCalendarProvider);
    }
}