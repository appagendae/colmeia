package com.agendae.colmeia.infra.adapter.in.web;

import com.agendae.colmeia.domain.port.in.SyncCalendar;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("/api/v1/sync/google")
public class SyncController {

    private final SyncCalendar syncCalendar;

    public SyncController(SyncCalendar syncCalendar) {
        this.syncCalendar = syncCalendar;
    }

    /**
     * Inicia o fluxo de autenticação OAuth do Google.
     * * Esta rota agora corresponde exatamente ao chamado do frontend em index.html:
     * /api/v1/sync/google/auth
     * * Gera um 'state' (UUID) temporário para segurança (CSRF) e redireciona.
     */
    @GetMapping("/auth") // Rota corrigida: não exige {userId}
    public void googleAuthRedirect(HttpServletResponse response) throws IOException {
        // Gera um 'state' único para o fluxo, que deve ser validado no callback.
        // Isso garante a segurança do fluxo de autenticação.
        String tempState = UUID.randomUUID().toString();

        String authorizationUrl = syncCalendar.getGoogleAuthorizationUrl(tempState);
        response.sendRedirect(authorizationUrl);
    }

    /**
     * Endpoint de callback após o Google processar a autorização.
     * * O 'state' é usado para validar a sessão de segurança e recuperar o contexto do usuário.
     * Rota: /api/v1/sync/google/callback
     */
    @GetMapping("/callback")
    public ResponseEntity<String> googleCallback(@RequestParam("code") String code, @RequestParam("state") String state) {
        // O 'state' deve ser validado para garantir que o callback pertence a uma sessão iniciada.
        syncCalendar.handleGoogleCallback(state, code);
        // Retorno de sucesso (a lógica de redirecionamento final pode estar aqui ou no frontend).
        return ResponseEntity.ok("Successfully authenticated with Google. You can close this window.");
    }
}