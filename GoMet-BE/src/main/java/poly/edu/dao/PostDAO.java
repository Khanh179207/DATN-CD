package poly.edu.dao;

import poly.edu.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostDAO extends JpaRepository<Post, Integer> {

    List<Post> findByIsApproved(Integer isApproved);

    List<Post> findByAccount_AccountID(Integer accountID);

    List<Post> findByCategory_CategoryID(Integer categoryID);


}
