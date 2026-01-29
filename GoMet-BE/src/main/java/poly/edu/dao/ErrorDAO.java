package poly.edu.dao;

import poly.edu.entity.Error;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ErrorDAO extends JpaRepository<Error, Integer> {
}
