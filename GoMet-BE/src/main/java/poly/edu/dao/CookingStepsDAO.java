package poly.edu.dao;

import poly.edu.entity.CookingSteps;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CookingStepsDAO extends JpaRepository<CookingSteps, Integer> {
    List<CookingSteps> findByPost_PostIDOrderByStepNumberAsc(Integer postID);
}
