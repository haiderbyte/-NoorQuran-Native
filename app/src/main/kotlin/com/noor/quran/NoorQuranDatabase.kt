package com.noor.quran

import android.content.Context
import androidx.room.*

@Entity(tableName = "surahs")
data class SurahEntity(
    @PrimaryKey val id: Int,
    val nameArabic: String,
    val revelationType: String,
    val versesCount: Int
)

@Entity(
    tableName = "verses",
    primaryKeys = ["surah_number", "ayah_number"]
)
data class VerseEntity(
    val surah_number: Int,
    val ayah_number: Int,
    val text: String,
    val tafsir_text: String
)

@Entity(tableName = "bookmarks")
data class BookmarkEntity(
    @PrimaryKey val surah_number: Int,
    val ayah_number: Int,
    val timestamp: Long = System.currentTimeMillis()
)

@Database(
    entities = [SurahEntity::class, VerseEntity::class, BookmarkEntity::class],
    version = 1,
    exportSchema = false
)
abstract class NoorQuranDatabase : RoomDatabase() {
    abstract fun quranDao(): QuranDao

    companion object {
        @Volatile
        private var INSTANCE: NoorQuranDatabase? = null

        fun getDatabase(context: Context): NoorQuranDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NoorQuranDatabase::class.java,
                    "NoorQuran.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
