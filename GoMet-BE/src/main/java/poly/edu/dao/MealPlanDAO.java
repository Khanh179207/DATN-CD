package poly.edu.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import poly.edu.entity.MealPlan;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MealPlanDAO extends JpaRepository<MealPlan, Integer> {

    // Đã sửa chữ AccountId thành AccountID (Viết hoa chữ D)
    List<MealPlan> findByAccount_AccountIDAndPlanDateOrderByCreatedAtAsc(Integer accountId, LocalDate planDate);
}