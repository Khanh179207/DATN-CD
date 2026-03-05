package poly.edu.service;

public interface NoteService {
    String getNoteContent(Integer accountId, Integer postId);
    void saveOrUpdateNote(Integer accountId, Integer postId, String content);
}