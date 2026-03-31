package poly.edu.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import poly.edu.entity.InteractionLog;

public interface InteractionLogDAO extends JpaRepository<InteractionLog, Integer>, InteractionLogDAOCustom {

    @Modifying
    @Transactional
    @Query("DELETE FROM InteractionLog i WHERE i.referenceId = :refId AND i.type = :type")
    void deleteByReference(@Param("refId") Integer refId, @Param("type") String type);

}
