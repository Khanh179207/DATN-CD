package poly.edu.service;

import poly.edu.dto.RewardedUserDTO;
import java.util.List;

/**
 * Service for handling event rewards to top participants
 * Uses event.winner field as reward flag (NULL = not rewarded, non-NULL = already rewarded)
 * Rewards top 3 users with points: Rank 1: +3, Rank 2: +2, Rank 3: +1
 */
public interface EventRewardService {

    /**
     * Reward top 3 users for a completed event
     * Only rewards once per event (checked via event.winner flag)
     * Prevents duplicate rewards if one user has multiple posts (takes best post only)
     *
     * @param eventId the ID of the event to reward for
     * @return list of rewarded users with their reward details
     * @throws IllegalArgumentException if event not found
     * @throws IllegalStateException if event has already been rewarded or not ended
     */
    List<RewardedUserDTO> rewardTopUsersForEvent(Integer eventId);

    /**
     * Check if an event is eligible for rewards
     *
     * @param eventId the ID of the event
     * @return true if event can be rewarded (voteEndAt passed and winner is NULL)
     */
    boolean isEventEligibleForReward(Integer eventId);
}
