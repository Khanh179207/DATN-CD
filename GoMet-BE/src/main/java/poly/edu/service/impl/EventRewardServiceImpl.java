package poly.edu.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import poly.edu.dao.EventDAO;
import poly.edu.dao.EventPostsDAO;
import poly.edu.dao.AccountDAO;
import poly.edu.dto.RewardedUserDTO;
import poly.edu.entity.Event;
import poly.edu.entity.EventPosts;
import poly.edu.entity.Account;
import poly.edu.service.EventRewardService;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service implementation for handling event rewards
 * Uses event.winner field as reward flag (NULL = not rewarded, non-NULL = already rewarded)
 * Rewards top 3 users when an event ends with points:
 * - Rank 1: +3 points
 * - Rank 2: +2 points
 * - Rank 3: +1 point
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EventRewardServiceImpl implements EventRewardService {

    private final EventDAO eventDAO;
    private final EventPostsDAO eventPostsDAO;
    private final AccountDAO accountDAO;

    private static final int RANK_1_POINTS = 3;
    private static final int RANK_2_POINTS = 2;
    private static final int RANK_3_POINTS = 1;
    private static final int MAX_REWARD_RANKS = 3;

    /**
     * Rewards top 3 users for a completed event
     * Transaction ensures atomicity - all or nothing
     * Only executes once per event (winner flag prevents duplicates)
     * Avoids duplicate rewards if one user has multiple posts (takes best post only)
     *
     * @param eventId the ID of the event to reward for
     * @return list of rewarded users with their reward details
     * @throws IllegalArgumentException if event not found
     * @throws IllegalStateException if event has already been rewarded or not ended
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<RewardedUserDTO> rewardTopUsersForEvent(Integer eventId) {
        log.info("🎯 Starting reward process for event ID: {}", eventId);

        // 1. Find the event
        Event event = eventDAO.findById(eventId)
                .orElseThrow(() -> {
                    log.error("❌ Event not found with ID: {}", eventId);
                    return new IllegalArgumentException("Event not found with ID: " + eventId);
                });

        // 2. Check if event has already been rewarded (winner != null means rewarded)
        if (event.getWinner() != null) {
            log.warn("⚠️ Event ID: {} has already been rewarded (winner: {})", eventId, event.getWinner());
            throw new IllegalStateException("Event has already been rewarded");
        }

        // 3. Check if event has ended (check voteEndAt first, then endAt)
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endTime = event.getVoteEndAt() != null ? event.getVoteEndAt() : event.getEndAt();

        if (endTime == null || now.isBefore(endTime)) {
            log.warn("⏰ Event ID: {} has not ended yet. End time: {}, Current time: {}", eventId, endTime, now);
            throw new IllegalStateException("Event has not ended yet. End time: " + endTime);
        }

        log.info("✅ Event eligibility check passed");

        // 4. Get all event posts sorted by voteCount (descending)
        List<EventPosts> eventPosts = eventPostsDAO.findByEvent_EventID(eventId);
        log.info("📊 Found {} event posts for event ID: {}", eventPosts.size(), eventId);

        if (eventPosts.isEmpty()) {
            log.info("📭 No posts found for event ID: {}. Marking event as rewarded with no recipients.", eventId);
            event.setWinner(0); // Use 0 to mark as rewarded but no winner
            eventDAO.save(event);
            return Collections.emptyList();
        }

        // 5. Group by account and take the best post (highest voteCount) for each account
        // This prevents the same user from being rewarded multiple times
        Map<Integer, EventPosts> bestPostByAccount = eventPosts.stream()
                .collect(Collectors.toMap(
                        ep -> ep.getPost().getAccount().getAccountID(),
                        ep -> ep,
                        (existing, newer) -> {
                            // Keep the post with higher voteCount
                            int existingVotes = existing.getVoteCount() != null ? existing.getVoteCount() : 0;
                            int newerVotes = newer.getVoteCount() != null ? newer.getVoteCount() : 0;
                            return newerVotes > existingVotes ? newer : existing;
                        }
                ));

        log.info("👥 Unique participants after deduplication: {}", bestPostByAccount.size());

        // 6. Sort by voteCount descending and take top 3
        List<EventPosts> topPosts = bestPostByAccount.values().stream()
                .sorted((ep1, ep2) -> {
                    int votes1 = ep1.getVoteCount() != null ? ep1.getVoteCount() : 0;
                    int votes2 = ep2.getVoteCount() != null ? ep2.getVoteCount() : 0;
                    return Integer.compare(votes2, votes1); // Descending
                })
                .limit(MAX_REWARD_RANKS)
                .collect(Collectors.toList());

        log.info("🏆 Top {} posts selected for reward", topPosts.size());

        // 7. Reward points and track rewarded users
        List<RewardedUserDTO> rewardedUsers = new ArrayList<>();
        List<Account> accountsToUpdate = new ArrayList<>();
        Integer topWinnerAccountID = null;

        for (int rank = 0; rank < topPosts.size(); rank++) {
            EventPosts topPost = topPosts.get(rank);
            Account account = topPost.getPost().getAccount();
            int rewardRank = rank + 1; // 1-based rank
            int pointsToAdd = getPointsForRank(rewardRank);

            // Store top winner's account ID
            if (rank == 0) {
                topWinnerAccountID = account.getAccountID();
            }

            // Update account points
            Integer currentPoints = account.getPoint() != null ? account.getPoint() : 0;
            account.setPoint(currentPoints + pointsToAdd);
            accountsToUpdate.add(account);

            log.info("🎁 Rewarding account ID: {} ({}) with {} points (Rank: #{})",
                    account.getAccountID(), account.getUsername(), pointsToAdd, rewardRank);

            // Create reward DTO
            RewardedUserDTO rewardedUser = RewardedUserDTO.builder()
                    .accountID(account.getAccountID())
                    .username(account.getUsername())
                    .avatar(account.getAvatar())
                    .rank(rewardRank)
                    .pointsRewarded(pointsToAdd)
                    .voteCount(topPost.getVoteCount() != null ? topPost.getVoteCount() : 0)
                    .postID(topPost.getPost().getPostID())
                    .build();

            rewardedUsers.add(rewardedUser);
        }

        // 8. Save updated accounts
        accountDAO.saveAll(accountsToUpdate);
        log.info("💾 Updated {} accounts with reward points", accountsToUpdate.size());

        // 9. Mark event as rewarded by setting winner to top 1 account ID
        if (topWinnerAccountID != null) {
            event.setWinner(topWinnerAccountID);
            log.info("👑 Set event winner to account ID: {}", topWinnerAccountID);
        }
        // event.setUpdatedAt(LocalDateTime.now());
        eventDAO.save(event);

        log.info("✨ Event ID: {} has been successfully rewarded. Rewarded {} users", eventId, rewardedUsers.size());
        return rewardedUsers;
    }

    /**
     * Check if an event is eligible for rewards
     *
     * @param eventId the ID of the event
     * @return true if event can be rewarded (voteEndAt passed and winner is NULL)
     */
    @Override
    public boolean isEventEligibleForReward(Integer eventId) {
        Optional<Event> eventOpt = eventDAO.findById(eventId);

        if (eventOpt.isEmpty()) {
            log.debug("❌ Event not found with ID: {}", eventId);
            return false;
        }

        Event event = eventOpt.get();

        // Check if already rewarded (winner != null)
        if (event.getWinner() != null) {
            log.debug("⚠️ Event ID: {} already rewarded (winner: {})", eventId, event.getWinner());
            return false;
        }

        // Check if event has ended
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endTime = event.getVoteEndAt() != null ? event.getVoteEndAt() : event.getEndAt();

        if (endTime == null || now.isBefore(endTime)) {
            log.debug("⏰ Event ID: {} has not ended yet. End time: {}", eventId, endTime);
            return false;
        }

        return true;
    }

    /**
     * Get reward points for a specific rank
     *
     * @param rank 1, 2, or 3
     * @return points to reward
     * @throws IllegalArgumentException if rank is invalid
     */
    private int getPointsForRank(int rank) {
        return switch (rank) {
            case 1 -> RANK_1_POINTS;
            case 2 -> RANK_2_POINTS;
            case 3 -> RANK_3_POINTS;
            default -> throw new IllegalArgumentException("Invalid rank: " + rank);
        };
    }
}
