package poly.edu.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import poly.edu.entity.SystemSetting;

public interface SystemSettingDAO extends JpaRepository<SystemSetting, String> {
}
