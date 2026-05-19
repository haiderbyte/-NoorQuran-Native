package com.noor.quran

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface QuranDao {
    @Query("SELECT * FROM surahs ORDER BY id ASC")
    fun getAllSurahs(): Flow<List<SurahEntity>>

    @Query("SELECT * FROM surahs WHERE id = :surahId")
    suspend fun getSurahById(surahId: Int): SurahEntity?

    @Query("SELECT * FROM verses WHERE surah_number = :surahId ORDER BY ayah_number ASC")
    fun getVersesForSurah(surahId: Int): Flow<List<VerseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveBookmark(bookmark: BookmarkEntity)

    @Query("DELETE FROM bookmarks WHERE surah_number = :surahId")
    suspend fun clearBookmarkForSurah(surahId: Int)

    @Query("SELECT * FROM bookmarks ORDER BY timestamp DESC LIMIT 1")
    fun getLastReadBookmark(): Flow<BookmarkEntity?>

    @Query("SELECT * FROM bookmarks WHERE surah_number = :surahId LIMIT 1")
    suspend fun getBookmarkForSurah(surahId: Int): BookmarkEntity?
}
