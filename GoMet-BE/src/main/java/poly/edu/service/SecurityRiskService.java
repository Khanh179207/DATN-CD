package poly.edu.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import poly.edu.dao.AccountDAO;
import poly.edu.dto.SecurityRiskDTO;
import poly.edu.dto.SessionDTO;
import poly.edu.entity.Account;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SecurityRiskService {

    private final AccountDAO accountDAO;
    @Lazy
    private final TokenService tokenService;
    private final TrustedDeviceService trustedDeviceService;
    private final RiskEngine riskEngine;

    public Optional<SecurityRiskDTO> getCurrentRiskForUser(Integer accountId) {
        if (accountId == null) {
            return Optional.empty();
        }

        Account account = accountDAO.findById(accountId).orElse(null);
        if (account == null) {
            return Optional.empty();
        }

        List<SessionDTO> sessions = tokenService.listActiveSessions(accountId, null);
        SessionDTO latestSession = sessions.stream()
                .max(Comparator.comparing(session -> {
                    Instant candidate = session.getLastUsedAt() != null ? session.getLastUsedAt() : session.getCreatedAt();
                    return candidate != null ? candidate : Instant.EPOCH;
                }))
                .orElse(null);

        String latestDeviceId = latestSession != null && latestSession.getDeviceId() != null
                ? latestSession.getDeviceId()
                : account.getLastLoginDeviceId();
        String latestIp = latestSession != null && latestSession.getIp() != null
                ? latestSession.getIp()
                : account.getLastLoginIp();
        Instant latestActivityAt = latestSession != null
                ? (latestSession.getLastUsedAt() != null ? latestSession.getLastUsedAt() : latestSession.getCreatedAt())
                : account.getLastLoginAt();

        boolean hasNoDeviceHistory = account.getLastLoginDeviceId() == null;
        boolean deviceTrusted = latestDeviceId != null && trustedDeviceService.isDeviceTrusted(accountId, latestDeviceId);
        boolean sameDeviceAsLast = latestDeviceId != null && latestDeviceId.equals(account.getLastLoginDeviceId());
        boolean ipChanged = latestIp != null && account.getLastLoginIp() != null && !latestIp.equals(account.getLastLoginIp());
        int trustedDeviceCount = trustedDeviceService.listTrustedDevices(accountId).size();

        RiskEngine.RiskLevel level = riskEngine.assess(account, latestDeviceId, latestIp != null ? latestIp : "");
        boolean requiresVerification = riskEngine.requiresVerification(level, account, latestDeviceId);

        LinkedHashSet<String> reasons = new LinkedHashSet<>();
        if (deviceTrusted) reasons.add("trusted_device");
        if (hasNoDeviceHistory) reasons.add("no_device_history");
        if (sameDeviceAsLast) reasons.add("same_device_as_last");
        if (ipChanged) reasons.add("ip_changed");
        if (latestDeviceId == null || latestDeviceId.isBlank()) reasons.add("missing_device_fingerprint");
        if (trustedDeviceCount == 0) reasons.add("no_trusted_devices");
        if (sessions.size() >= 4) reasons.add("many_active_sessions");
        if (!account.isMfaEnabled()) reasons.add("mfa_disabled");
        if (requiresVerification) reasons.add("verification_required");

        LinkedHashSet<String> recommendedActions = new LinkedHashSet<>();
        if (!account.isMfaEnabled()) recommendedActions.add("enable_mfa");
        if (latestDeviceId != null && !deviceTrusted) recommendedActions.add("review_latest_device");
        if (ipChanged || requiresVerification) recommendedActions.add("review_recent_ip");
        if (sessions.size() >= 3) recommendedActions.add("review_sessions");
        if (trustedDeviceCount == 0) recommendedActions.add("add_trusted_device");

        return Optional.of(SecurityRiskDTO.builder()
                .riskLevel(level.name())
                .requiresVerification(requiresVerification)
                .deviceTrusted(deviceTrusted)
                .hasNoDeviceHistory(hasNoDeviceHistory)
                .sameDeviceAsLast(sameDeviceAsLast)
                .ipChanged(ipChanged)
                .latestDeviceId(latestDeviceId)
                .latestIp(latestIp)
                .latestActivityAt(latestActivityAt)
                .activeSessionCount(sessions.size())
                .trustedDeviceCount(trustedDeviceCount)
                .reasons(new ArrayList<>(reasons))
                .recommendedActions(new ArrayList<>(recommendedActions))
                .build());
    }
}