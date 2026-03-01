package poly.edu.service;

import poly.edu.dto.MealPlanDTO;
import java.time.LocalDate;
import java.util.List;

public interface MealPlanService {
    MealPlanDTO createMealPlan(Integer accountId, MealPlanDTO dto);
    List<MealPlanDTO> getMealPlansByDate(Integer accountId, LocalDate date);
    MealPlanDTO markAsCompleted(Integer planId);
    void deleteMealPlan(Integer planId);
}