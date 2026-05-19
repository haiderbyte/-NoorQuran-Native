package com.noor.quran

import kotlinx.coroutines.flow.Flow

class QuranRepository(private val quranDao: QuranDao) {

    fun getAllSurahs(): Flow<List<SurahEntity>> = quranDao.getAllSurahs()

    fun getVersesForSurah(surahId: Int): Flow<List<VerseEntity>> = 
        quranDao.getVersesForSurah(surahId)

    suspend fun getSurahById(surahId: Int): SurahEntity? = 
        quranDao.getSurahById(surahId)

    suspend fun saveBookmark(surahId: Int, ayahId: Int) {
        quranDao.clearBookmarkForSurah(surahId)
        quranDao.saveBookmark(BookmarkEntity(surahId, ayahId))
    }

    fun getLastRead(): Flow<BookmarkEntity?> = quranDao.getLastReadBookmark()
}
