package poly.edu.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import poly.edu.dao.TrustedDeviceDAO;
import poly.edu.dto.TrustedDeviceDTO;
import poly.edu.entity.TrustedDevice;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Phase 2 — Trusted Device Management.
 *
 * A device becomes trusted after the user clicks the email verification link.
 * Once trusted, subsequent logins from the same device+user combo do not
 * trigger the suspicious-login email.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TrustedDeviceService {

    private final TrustedDeviceDAO trustedDeviceDAO;

    // ─── Queries ──────────────────────────────────────────────────────────────

    public boolean isDeviceTrusted(Integer userId, String deviceId) {
        if (deviceId == null || deviceId.isBlank()) return false;
        return trustedDeviceDAO.findByUserIdAndDeviceId(userId, deviceId)
                .map(TrustedDevice::isActive)
                .orElse(false);
    }

    public Optional<TrustedDevice> find(Integer userId, String deviceId) {
        return trustedDeviceDAO.findByUserIdAndDeviceId(userId, deviceId);
    }

    // ─── Mutations ────────────────────────────────────────────────────────────

    /**
     * Records the device as seen. If it already exists, updates last-seen/IP.
     * Does NOT set isTrusted=true — that happens only via email verification.
     */
    @Transactional
    public TrustedDevice recordSeen(Integer userId, String deviceId, String deviceName,
                                    String userAgent, String ip) {
        String uaHash = RateLimitService.sha256(userAgent);
        Optional<TrustedDevice> existing = trustedDeviceDAO.findByUserIdAndDeviceId(userId, deviceId);

        if (existing.isPresent()) {
            TrustedDevice d = existing.get();
            trustedDeviceDAO.updateLastSeen(d.getId(), Instant.now(), ip);
            d.setLastSeenAt(Instant.now());
            d.setLastIp(ip);
            return d;
        }

        TrustedDevice device = TrustedDevice.builder()
                .userId(userId)
                .deviceId(deviceId)
                .deviceName(deviceName != null ? deviceName : RateLimitService.inferDeviceName(userAgent))
                .userAgentHash(uaHash)
                .firstSeenAt(Instant.now())
                .lastSeenAt(Instant.now())
                .lastIp(ip)
                .isTrusted(false) // not trusted until email verified
                .build();

        return trustedDeviceDAO.save(device);
    }

    /**
     * Marks a device as trusted (called after email magic-link verification).
     */
    @Transactional
    public void trustDevice(Integer userId, String deviceId) {
        if (deviceId == null || deviceId.isBlank()) return;

        TrustedDevice device = trustedDeviceDAO.findByUserIdAndDeviceId(userId, deviceId)
                .orElseGet(() -> TrustedDevice.builder()
                        .userId(userId)
                        .deviceId(deviceId)
                        .deviceName("Trusted device")
                        .userAgentHash("unknown")
                        .firstSeenAt(Instant.now())
                        .lastSeenAt(Instant.now())
                        .isTrusted(false)
                        .build());

        device.setTrusted(true);
        device.setRevokedAt(null);
        device.setLastSeenAt(Instant.now());
        trustedDeviceDAO.save(device);
        log.info("Device {} trusted for user {}", deviceId, userId);
    }

    /**
     * Revoke a specific device (user or admin action).
     */
    @Transactional
    public boolean revokeDevice(Long deviceRowId, Integer userId) {
        return trustedDeviceDAO.findById(deviceRowId).map(d -> {
            if (!d.getUserId().equals(userId)) return false;
            d.setRevokedAt(Instant.now());
            d.setTrusted(false);
            trustedDeviceDAO.save(d);
            log.info("Device {} revoked for user {}", deviceRowId, userId);
            return true;
        }).orElse(false);
    }

    /**
     * Revoke ALL devices for a user (triggered by "this wasn't me" flow).
     */
    @Transactional
    public void revokeAllDevices(Integer userId) {
        trustedDeviceDAO.revokeAllForUser(userId, Instant.now());
        log.info("All devices revoked for user {}", userId);
    }

    // ─── List ─────────────────────────────────────────────────────────────────

    public List<TrustedDeviceDTO> listTrustedDevices(Integer userId) {
        return trustedDeviceDAO.findByUserIdAndIsTrustedTrueAndRevokedAtIsNull(userId).stream()
                .map(d -> TrustedDeviceDTO.builder()
                        .id(d.getId())
                        .deviceId(d.getDeviceId())
                        .deviceName(d.getDeviceName())
                        .lastIp(d.getLastIp())
                        .firstSeenAt(d.getFirstSeenAt())
                        .lastSeenAt(d.getLastSeenAt())
                        .trusted(d.isActive())
                        .build())
                .collect(Collectors.toList());
    }
}
