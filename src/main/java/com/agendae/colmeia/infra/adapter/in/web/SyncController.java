package com.agendae.colmeia.infra.adapter.in.web;

import com.agendae.colmeia.domain.port.in.SyncCalendar;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
// DEFINIÇÃO DE ROTA MAIS CLARA E AGRUPADA
@RequestMapping("/api/v1/sync/google")
public class SyncController {

    private final SyncCalendar syncCalendar;

    public SyncController(SyncCalendar syncCalendar) {
        this.syncCalendar = syncCalendar;
    }

    // O caminho agora é apenas o que varia: "/auth/{userId}"
    @GetMapping("/auth/{userId}")
    public void googleAuthRedirect(@PathVariable UUID userId, HttpServletResponse response) throws IOException {
        String authorizationUrl = syncCalendar.getGoogleAuthorizationUrl(userId.toString());
        response.sendRedirect(authorizationUrl);
    }

    // O caminho agora é apenas "/callback"
    @GetMapping("/callback")
    public ResponseEntity<String> googleCallback(@RequestParam("code") String code, @RequestParam("state") String state) {
        // O 'state' deve ser validado e usado para recuperar o userId
        // UUID userId = UUID.fromString(state); // passando o state direto
        syncCalendar.handleGoogleCallback(state, code);
        return ResponseEntity.ok("Successfully authenticated with Google. You can close this window.");
    }
}