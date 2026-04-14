package poly.edu.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import poly.edu.dao.EventDAO;
import poly.edu.dao.EventPostsDAO;
import poly.edu.dao.AccountDAO;
import poly.edu.dao.SubscriptionDAO;
import poly.edu.dto.RewardedUserDTO;
import poly.edu.entity.Event;
import poly.edu.entity.EventPosts;
import poly.edu.entity.Account;
import poly.edu.entity.Subscription;
import poly.edu.service.EventRewardService;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventRewardServiceImpl implements EventRewardService {

    private final EventDAO eventDAO;
    private final EventPostsDAO eventPostsDAO;
    private final AccountDAO accountDAO;
    private final SubscriptionDAO subscriptionDAO;

    private static final int MAX_REWARD_RANKS = 3;

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

        // 2. Check if already rewarded
        if (event.getWinner() != null) {
            log.warn("⚠️ Event ID: {} has already been rewarded", eventId);
            throw new IllegalStateException("Event has already been rewarded");
        }

        // 3. Check if event has ended
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endTime = event.getVoteEndAt() != null ? event.getVoteEndAt() : event.getEndAt();

        if (endTime == null || now.isBefore(endTime)) {
            log.warn("⏰ Event ID: {} has not ended yet", eventId);
            throw new IllegalStateException("Event has not ended yet");
        }

        log.info("✅ Event eligibility check passed");

        // 4. Get all event posts
        List<EventPosts> eventPosts = eventPostsDAO.findByEvent_EventID(eventId);
        log.info("📊 Found {} event posts", eventPosts.size());

        if (eventPosts.isEmpty()) {
            log.info("📭 No posts found for event");
            event.setWinner(0);
            eventDAO.save(event);
            return Collections.emptyList();
        }

        // 5. Group by account, take best post per user
        Map<Integer, EventPosts> bestPostByAccount = eventPosts.stream()
                .collect(Collectors.toMap(
                        ep -> ep.getPost().getAccount().getAccountID(),
                        ep -> ep,
                        (existing, newer) -> {
                            int existingVotes = existing.getVoteCount() != null ? existing.getVoteCount() : 0;
                            int newerVotes = newer.getVoteCount() != null ? newer.getVoteCount() : 0;
                            return newerVotes > existingVotes ? newer : existing;
                        }));

        // 6. Sort and get top 3
        List<EventPosts> topPosts = bestPostByAccount.values().stream()
                .sorted((ep1, ep2) -> Integer.compare(
                        ep2.getVoteCount() != null ? ep2.getVoteCount() : 0,
                        ep1.getVoteCount() != null ? ep1.getVoteCount() : 0))
                .limit(MAX_REWARD_RANKS)
                .collect(Collectors.toList());

        log.info("🏆 Top {} posts selected", topPosts.size());

        // 7. Parse reward config
        RewardConfig rewardConfig = parseRewardConfig(event.getReward());
        log.info("💰 Reward type: {}", rewardConfig.type);

        // 8. Apply rewards
        List<RewardedUserDTO> rewardedUsers = new ArrayList<>();
        List<Account> accountsToUpdate = new ArrayList<>();
        List<Subscription> subscriptionsToCreate = new ArrayList<>();
        Integer topWinnerAccountID = null;

        for (int rank = 0; rank < topPosts.size(); rank++) {
            EventPosts topPost = topPosts.get(rank);
            Account account = topPost.getPost().getAccount();
            int rewardRank = rank + 1;

            if (rank == 0) {
                topWinnerAccountID = account.getAccountID();
            }

            // Apply reward based on type
            if ("PREMIUM".equals(rewardConfig.type) || "PREMIUM_1M".equals(rewardConfig.type)
                    || "PREMIUM_1Y".equals(rewardConfig.type)) {
                int premiumDays = rewardConfig.getPremiumDaysForRank(rewardRank);
                if (premiumDays > 0) {
                    // Create subscription record
                    LocalDateTime startDate = LocalDateTime.now();
                    LocalDateTime endDate = startDate.plusDays(premiumDays);

                    Subscription subscription = Subscription.builder()
                            .account(account)
                            .planType(1) // Premium plan
                            .startAt(startDate)
                            .endAt(endDate)
                            .isActive(1)
                            .build();

                    subscriptionsToCreate.add(subscription);
                    account.setIsPremium(1);

                    log.info("🎁 Rank #{}: Premium reward {} days for account {}",
                            rewardRank, premiumDays, account.getAccountID());
                }
            } else if ("POINTS".equals(rewardConfig.type)) {
                int points = rewardConfig.getPointsForRank(rewardRank);
                if (points > 0) {
                    Integer currentPoints = account.getPoint() != null ? account.getPoint() : 0;
                    account.setPoint(currentPoints + points);

                    log.info("🎁 Rank #{}: Points reward {} for account {}",
                            rewardRank, points, account.getAccountID());
                }
            }

            accountsToUpdate.add(account);

            // Create DTO
            RewardedUserDTO dto = RewardedUserDTO.builder()
                    .accountID(account.getAccountID())
                    .username(account.getUsername())
                    .avatar(account.getAvatar())
                    .rank(rewardRank)
                    .pointsRewarded("PREMIUM".equals(rewardConfig.type) || "PREMIUM_1M".equals(rewardConfig.type)
                            || "PREMIUM_1Y".equals(rewardConfig.type) ? rewardConfig.getPremiumDaysForRank(rewardRank)
                                    : rewardConfig.getPointsForRank(rewardRank))
                    .voteCount(topPost.getVoteCount() != null ? topPost.getVoteCount() : 0)
                    .postID(topPost.getPost().getPostID())
                    .build();

            rewardedUsers.add(dto);
        }

        // 9. Save all changes
        accountDAO.saveAll(accountsToUpdate);
        subscriptionDAO.saveAll(subscriptionsToCreate);

        if (topWinnerAccountID != null) {
            event.setWinner(topWinnerAccountID);
        }
        eventDAO.save(event);

        log.info("✨ Event ID: {} rewarded {} users", eventId, rewardedUsers.size());
        return rewardedUsers;
    }

    @Override
    public boolean isEventEligibleForReward(Integer eventId) {
        Optional<Event> eventOpt = eventDAO.findById(eventId);

        if (eventOpt.isEmpty()) {
            return false;
        }

        Event event = eventOpt.get();

        if (event.getWinner() != null) {
            return false;
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endTime = event.getVoteEndAt() != null ? event.getVoteEndAt() : event.getEndAt();

        if (endTime == null || now.isBefore(endTime)) {
            return false;
        }

        return true;
    }

    private RewardConfig parseRewardConfig(String reward) {
        if (reward == null || reward.trim().isEmpty()) {
            return new RewardConfig("NONE", 0, 0, 0, 0, 0, 0);
        }

        String[] parts = reward.split("\\|");
        String type = parts[0];

        // 1. Xử lý giải PREMIUM (Chỉ có gói cố định, không cần tham số top1, top2, top3)
        if ("PREMIUM_1M".equals(type) || "PREMIUM_1Y".equals(type)) {
            return new RewardConfig(type, 0, 0, 0, 0, 0, 0);
        }

        // Nếu format không phải các chuẩn quy định (ví dụ là một custom string như "Tặng 500k")
        // thì ta coi như giải thưởng thủ công (NONE), không báo lỗi gắt, chỉ log INFO.
        if (parts.length < 4 && !"PREMIUM".equals(type) && !"POINTS".equals(type)) {
            log.info("ℹ️ Event sử dụng giải thưởng tuỳ chỉnh (không tự động trao thưởng): {}", reward);
            return new RewardConfig("NONE", 0, 0, 0, 0, 0, 0);
        } else if (parts.length < 4) {
             // Trường hợp dùng loại PREMIUM hoặc POINTS nhưng sai cấu trúc
             log.error("❌ Sai format phần thưởng tự động ({}", reward);
             return new RewardConfig("NONE", 0, 0, 0, 0, 0, 0);
        }

        try {
            int val1 = Integer.parseInt(parts[1]);
            int val2 = Integer.parseInt(parts[2]);
            int val3 = Integer.parseInt(parts[3]);
            return new RewardConfig(type, val1, val2, val3, 0, 0, 0);
        } catch (NumberFormatException e) {
            log.error("❌ Lỗi parse số liệu phần thưởng: {}", reward);
            return new RewardConfig("NONE", 0, 0, 0, 0, 0, 0);
        }
    }

    // Inner class to hold reward configuration
    private static class RewardConfig {
        String type;
        int top1Value;
        int top2Value;
        int top3Value;

        RewardConfig(String type, int top1, int top2, int top3, int dummy1, int dummy2, int dummy3) {
            this.type = type;
            this.top1Value = top1;
            this.top2Value = top2;
            this.top3Value = top3;
        }

        int getPremiumDaysForRank(int rank) {
            // Cả 3 Top đều nhận 30 ngày
            if ("PREMIUM_1M".equals(type)) {
                return 30;
            }
            // Cả 3 Top đều nhận 365 ngày
            if ("PREMIUM_1Y".equals(type)) {
                return 365;
            }

            // (Phòng hờ dữ liệu cũ trong DB đang lưu chữ "PREMIUM|30|15|7")
            if ("PREMIUM".equals(type)) {
                return switch (rank) {
                    case 1 -> top1Value;
                    case 2 -> top2Value;
                    case 3 -> top3Value;
                    default -> 0;
                };
            }
            return 0;
        }

        int getPointsForRank(int rank) {
            // Tùy theo top mà lấy Point tương ứng
            if ("POINTS".equals(type)) {
                return switch (rank) {
                    case 1 -> top1Value;
                    case 2 -> top2Value;
                    case 3 -> top3Value;
                    default -> 0;
                };
            }
            return 0;
        }
    }
}

