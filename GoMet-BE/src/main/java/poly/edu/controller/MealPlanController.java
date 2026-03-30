package poly.edu.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import poly.edu.dto.MealPlanDTO;
import poly.edu.service.MealPlanService;

import java.time.LocalDate;
import java.util.List;


@RestController
@RequestMapping("/api/meal-plans")
public class MealPlanController {

    @Autowired
    private MealPlanService mealPlanService;

    // Tạo lịch ăn mới
    @PostMapping("/user/{accountId}")
    public ResponseEntity<MealPlanDTO> createMealPlan(
            @PathVariable Integer accountId,
            @RequestBody MealPlanDTO mealPlanDTO) {
        return ResponseEntity.ok(mealPlanService.createMealPlan(accountId, mealPlanDTO));
    }

    // Lấy danh sách món ăn trong 1 ngày
    @GetMapping("/user/{accountId}/date/{date}")
    public ResponseEntity<List<MealPlanDTO>> getMealPlansByDate(
            @PathVariable Integer accountId,
            @PathVariable String date) {
        return ResponseEntity.ok(mealPlanService.getMealPlansByDate(accountId, LocalDate.parse(date)));
    }

    // Đánh dấu tick hoàn thành
    @PutMapping("/{planId}/complete")
    public ResponseEntity<MealPlanDTO> markAsCompleted(@PathVariable Integer planId) {
        return ResponseEntity.ok(mealPlanService.markAsCompleted(planId));
    }

    // Xóa khỏi lịch
    @DeleteMapping("/{planId}")
    public ResponseEntity<Void> deleteMealPlan(@PathVariable Integer planId) {
        mealPlanService.deleteMealPlan(planId);
        return ResponseEntity.noContent().build();
    }
}