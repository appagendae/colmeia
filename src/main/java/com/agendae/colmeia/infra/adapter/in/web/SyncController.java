package com.agendae.colmeia.infra.adapter.in.web;

import com.agendae.colmeia.domain.port.in.SyncCalendar;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("/api/v1/sync")
@RequiredArgsConstructor
public class SyncController {

    private final SyncCalendar syncCalendar;

    @GetMapping("/google/auth/{userId}")
    public void googleAuthRedirect(@PathVariable UUID userId, HttpServletResponse response) throws IOException {
        String authorizationUrl = syncCalendar.getGoogleAuthorizationUrl(userId);
        response.sendRedirect(authorizationUrl);
    }

    @GetMapping("/google/callback")
    public ResponseEntity<String> googleCallback(@RequestParam("code") String code, @RequestParam("state") String state) {
        // O 'state' deveria ser validado e usado para recuperar o userId
        UUID userId = UUID.fromString(state); // Simplificação
        syncCalendar.handleGoogleCallback(userId, code);
        return ResponseEntity.ok("Successfully authenticated with Google. You can close this window.");
    }
}
