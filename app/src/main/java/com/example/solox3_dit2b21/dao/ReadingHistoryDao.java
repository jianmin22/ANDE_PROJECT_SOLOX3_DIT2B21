package com.example.solox3_dit2b21.dao;

import com.example.solox3_dit2b21.model.Chapter;
import com.example.solox3_dit2b21.model.SubChapter;

public interface ReadingHistoryDao {
    void updateOrCreateReadingHistory(String userId, String bookId, Chapter currentChapter, SubChapter currentSubChapter, String lastRead, DataCallback<Boolean> callback);

}
