package poly.edu.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import poly.edu.entity.SystemConfig;

public interface SystemConfigDAO extends JpaRepository<SystemConfig, String> {
    // Kế thừa sẵn findAll() và saveAll() rồi, không cần viết gì thêm!
}