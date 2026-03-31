package poly.edu.dao.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import poly.edu.dao.InteractionLogDAOCustom;
import poly.edu.entity.InteractionLog;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class InteractionLogDAOImpl implements InteractionLogDAOCustom {

    @PersistenceContext
    private EntityManager em;

    @Override
    @Transactional
    public void saveCustom(InteractionLog log) {
        em.persist(log);
    }

    @Override
    public List<Map<String, Object>> findTopTrending(LocalDateTime startDate, int limit) {
        String sql = """
            SELECT 
                p.PostID as id, 
                p.Title as title, 
                p.Media as image,
                a.Username as authorName, 
                p.Description as description,
                ISNULL(a.isPremium, 0) as isPremium,
                SUM(CASE 
                    WHEN i.Type = 'VIEW' THEN i.Value * 1 
                    WHEN i.Type = 'LIKE' THEN i.Value * 5 
                    WHEN i.Type = 'RATING' THEN i.Value * 10 
                    ELSE 0 END) as pts
            FROM Post p
            JOIN Account a ON p.AccountID = a.AccountID
            JOIN InteractionLog i ON p.PostID = i.PostID
            WHERE i.CreatedAt >= ?1
              AND p.isApproved = 1 
              AND p.isActive = 1
            GROUP BY p.PostID, p.Title, p.Media, a.Username, p.Description, a.isPremium
            ORDER BY pts DESC
            """;

        Query query = em.createNativeQuery(sql);
        query.setParameter(1, startDate);
        query.setMaxResults(limit);

        List<Object[]> results = query.getResultList();
        List<Map<String, Object>> list = new ArrayList<>();

        for (Object[] row : results) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", row[0]);
            map.put("title", row[1]);
            map.put("image", row[2]);
            map.put("authorName", row[3]);
            map.put("description", row[4]);
            map.put("isPremium", row[5]);
            map.put("pts", row[6]);
            list.add(map);
        }
        return list;
    }

}
