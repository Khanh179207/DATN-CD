package poly.edu.service;

import poly.edu.dto.RewardedUserDTO;
import java.util.List;

public interface EventRewardService {

    List<RewardedUserDTO> rewardTopUsersForEvent(Integer eventId);

    boolean isEventEligibleForReward(Integer eventId);
}
