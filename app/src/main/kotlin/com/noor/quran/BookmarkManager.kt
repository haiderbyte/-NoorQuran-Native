package com.noor.quran

import kotlinx.coroutines.flow.Flow

class BookmarkManager(private val repository: QuranRepository) {
    
    fun getLastRead(): Flow<BookmarkEntity?> {
        return repository.getLastRead()
    }

    suspend fun saveBookmark(surahId: Int, ayahId: Int) {
        repository.saveBookmark(surahId, ayahId)
    }
}
