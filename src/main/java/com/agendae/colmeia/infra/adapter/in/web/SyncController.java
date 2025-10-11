package com.agendae.colmeia.infra.adapter.in.web;

import com.agendae.colmeia.domain.port.in.SyncCalendar;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
// O @RequestMapping foi corrigido de "/api/v1/sync" para "/api/v1"
// para corresponder à chamada do frontend.
@RequestMapping("/api/v1")
public class SyncController {

    private final SyncCalendar syncCalendar;

    public SyncController(SyncCalendar syncCalendar) {
        this.syncCalendar = syncCalendar;
    }

    // O @GetMapping foi corrigido para incluir o caminho completo.
    @GetMapping("/sync/google/auth/{userId}")
    public void googleAuthRedirect(@PathVariable UUID userId, HttpServletResponse response) throws IOException {
        String authorizationUrl = syncCalendar.getGoogleAuthorizationUrl(userId);
        response.sendRedirect(authorizationUrl);
    }

    // O @GetMapping foi corrigido para incluir o caminho completo.
    @GetMapping("/sync/google/callback")
    public ResponseEntity<String> googleCallback(@RequestParam("code") String code, @RequestParam("state") String state) {
        // O 'state' deve ser validado e usado para recuperar o userId
        UUID userId = UUID.fromString(state); // Simplificação
        syncCalendar.handleGoogleCallback(userId, code);
        return ResponseEntity.ok("Successfully authenticated with Google. You can close this window.");
    }
}

