package poly.edu.service.impl;

import poly.edu.dao.NoteDAO;
import poly.edu.entity.Note;
import poly.edu.service.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;

@Service
public class NoteServiceImpl implements NoteService {

    @Autowired
    private NoteDAO noteDAO;

    @Override
    public String getNoteContent(Integer accountId, Integer postId) {
        return noteDAO.findByAccountIdAndPostIdAndIsActive(accountId, postId, 1)
                .map(Note::getContent)
                .orElse("");
    }

    @Override
    public void saveOrUpdateNote(Integer accountId, Integer postId, String content) {
        Note note = noteDAO.findByAccountIdAndPostIdAndIsActive(accountId, postId, 1)
                .orElse(new Note());

        if (note.getNoteId() == null) {
            note.setAccountId(accountId);
            note.setPostId(postId);
            note.setCreatedAt(LocalDate.now());
            note.setIsActive(1);
        }

        note.setContent(content);
        noteDAO.save(note);
    }
}