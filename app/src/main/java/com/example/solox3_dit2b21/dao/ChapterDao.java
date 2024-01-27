package com.example.solox3_dit2b21.dao;

import com.example.solox3_dit2b21.model.Chapter;
import java.util.List;

public interface ChapterDao {
    void fetchChapters(String bookId, DataCallback<List<Chapter>> callback);
    void saveChapters(String bookId, List<Chapter> chapters, DataStatusCallback callback);

    void getChaptersByBookId(String bookId, DataCallback<List<Chapter>> callback);
}
