package poly.edu.service;

import org.springframework.beans.factory.ObjectProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import poly.edu.dto.AuditLogDTO;
import poly.edu.dto.SecurityRiskDTO;
import poly.edu.dto.SecurityStreamEventDTO;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@Service
@RequiredArgsConstructor
public class SecurityEventStreamService {

    private static final long EMITTER_TIMEOUT_MS = 30L * 60L * 1000L;

    private final Map<Integer, CopyOnWriteArrayList<SseEmitter>> emittersByUser = new ConcurrentHashMap<>();
    private final ObjectProvider<SecurityRiskService> securityRiskServiceProvider;

    private static final Set<String> LIVE_EVENT_TYPES = Set.of(
            AuditLogService.LOGIN_SUCCESS,
            AuditLogService.LOGIN_SUSPICIOUS,
            AuditLogService.DEVICE_TRUSTED,
            AuditLogService.DEVICE_REVOKED,
            AuditLogService.DEVICE_REVOKE_ALL,
            AuditLogService.MFA_ENABLED,
            AuditLogService.MFA_DISABLED,
            AuditLogService.MFA_CHALLENGE_SUCCESS,
            AuditLogService.MFA_BACKUP_CODE_USED,
            AuditLogService.SESSION_REVOKE,
            AuditLogService.SESSION_REVOKE_ALL,
            AuditLogService.REFRESH_TOKEN_REUSE,
            AuditLogService.THIS_WASNT_ME,
            AuditLogService.PASSWORD_CHANGE,
            AuditLogService.PASSWORD_RESET_SUCCESS,
            AuditLogService.MAGIC_LINK_VERIFIED
    );

    public SseEmitter subscribe(Integer userId) {
        SseEmitter emitter = new SseEmitter(EMITTER_TIMEOUT_MS);
        emittersByUser.computeIfAbsent(userId, ignored -> new CopyOnWriteArrayList<>()).add(emitter);

        emitter.onCompletion(() -> unregister(userId, emitter));
        emitter.onTimeout(() -> unregister(userId, emitter));
        emitter.onError(error -> unregister(userId, emitter));

        try {
            emitter.send(SseEmitter.event()
                    .name("connected")
                    .data(Map.of("userId", userId, "status", "connected")));
        } catch (IOException e) {
            unregister(userId, emitter);
        }

        return emitter;
    }

    public void publish(AuditLogDTO event, Integer userId) {
        if (userId == null || event == null || !LIVE_EVENT_TYPES.contains(event.getEventType())) {
            return;
        }

        List<SseEmitter> emitters = emittersByUser.get(userId);
        if (emitters == null || emitters.isEmpty()) {
            return;
        }

        SecurityRiskService securityRiskService = securityRiskServiceProvider.getIfAvailable();
        SecurityRiskDTO risk = securityRiskService != null
            ? securityRiskService.getCurrentRiskForUser(userId).orElse(null)
            : null;
        SecurityStreamEventDTO payload = SecurityStreamEventDTO.builder()
                .event(event)
                .risk(risk)
                .build();

        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event()
                        .name("security-event")
                        .id(String.valueOf(event.getId()))
                        .data(payload));
            } catch (IOException ex) {
                unregister(userId, emitter);
                log.debug("Removed stale security SSE emitter for userId={}", userId);
            }
        }
    }

    private void unregister(Integer userId, SseEmitter emitter) {
        CopyOnWriteArrayList<SseEmitter> emitters = emittersByUser.get(userId);
        if (emitters == null) {
            return;
        }
        emitters.remove(emitter);
        if (emitters.isEmpty()) {
            emittersByUser.remove(userId);
        }
    }
}