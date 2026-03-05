package poly.edu.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import poly.edu.entity.TrustedDevice;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface TrustedDeviceDAO extends JpaRepository<TrustedDevice, Long> {

    Optional<TrustedDevice> findByUserIdAndDeviceId(Integer userId, String deviceId);

    List<TrustedDevice> findByUserIdAndIsTrustedTrueAndRevokedAtIsNull(Integer userId);

    /** Mark all trusted devices for a user as revoked (used on suspicious "this wasn't me"). */
    @Modifying
    @Transactional
    @Query("UPDATE TrustedDevice d SET d.revokedAt = :now WHERE d.userId = :userId AND d.revokedAt IS NULL")
    void revokeAllForUser(Integer userId, Instant now);

    /** Update last-seen timestamp and IP for an existing device. */
    @Modifying
    @Transactional
    @Query("UPDATE TrustedDevice d SET d.lastSeenAt = :now, d.lastIp = :ip WHERE d.id = :id")
    void updateLastSeen(Long id, Instant now, String ip);
}
