package poly.edu.dao;

import poly.edu.entity.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface NoteDAO extends JpaRepository<Note, Integer> {
    // Tìm ghi chú đang hoạt động (isActive = 1) theo User và Bài viết
    Optional<Note> findByAccountIdAndPostIdAndIsActive(Integer accountId, Integer postId, Integer isActive);
}