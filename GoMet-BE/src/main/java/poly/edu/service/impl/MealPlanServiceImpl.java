package poly.edu.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import poly.edu.dao.MealPlanDAO;
import poly.edu.dto.MealPlanDTO;
import poly.edu.entity.Account;
import poly.edu.entity.MealPlan;
import poly.edu.entity.Post;
import poly.edu.service.MealPlanService;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MealPlanServiceImpl implements MealPlanService {

    @Autowired
    private MealPlanDAO mealPlanDAO;

    @Override
    public MealPlanDTO createMealPlan(Integer accountId, MealPlanDTO dto) {
        MealPlan plan = new MealPlan();

        // Map Account (GỌI ĐÚNG HÀM setAccountID)
        Account account = new Account();
        account.setAccountID(accountId);
        plan.setAccount(account);

        // Map Post (GIẢ ĐỊNH BẢNG POST CŨNG DÙNG postID, nếu báo lỗi bạn đổi thành setPostId nhé)
        if (dto.getPostId() != null) {
            Post post = new Post();
            post.setPostID(dto.getPostId());
            plan.setPost(post);
        }

        plan.setCustomMealName(dto.getCustomMealName());
        plan.setPlanDate(dto.getPlanDate());
        plan.setMealType(dto.getMealType());
        plan.setNotes(dto.getNotes());

        // Nếu FE truyền true thì set 1, ngược lại mặc định là 0
        plan.setIsCompleted(dto.getIsCompleted() != null && dto.getIsCompleted() ? 1 : 0);

        MealPlan savedPlan = mealPlanDAO.save(plan);
        return mapToDTO(savedPlan);
    }

    @Override
    public List<MealPlanDTO> getMealPlansByDate(Integer accountId, LocalDate date) {
        List<MealPlan> plans = mealPlanDAO.findByAccount_AccountIDAndPlanDateOrderByCreatedAtAsc(accountId, date);
        return plans.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    public MealPlanDTO markAsCompleted(Integer planId) {
        MealPlan plan = mealPlanDAO.findById(planId).orElseThrow(() -> new RuntimeException("Kế hoạch không tồn tại"));
        plan.setIsCompleted(1);
        return mapToDTO(mealPlanDAO.save(plan));
    }

    @Override
    public void deleteMealPlan(Integer planId) {
        mealPlanDAO.deleteById(planId);
    }

    // Hàm chung để map Entity -> DTO
    private MealPlanDTO mapToDTO(MealPlan plan) {
        return MealPlanDTO.builder()
                .planId(plan.getPlanId())
                .accountId(plan.getAccount().getAccountID()) // GỌI ĐÚNG HÀM getAccountID
                .postId(plan.getPost() != null ? plan.getPost().getPostID() : null) // GỌI ĐÚNG HÀM getPostID
                .customMealName(plan.getCustomMealName())
                .planDate(plan.getPlanDate())
                .mealType(plan.getMealType())
                .notes(plan.getNotes())
                .isCompleted(plan.getIsCompleted() != null && plan.getIsCompleted() == 1)
                .build();
    }
}