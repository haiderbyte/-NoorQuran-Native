package com.noor.quran

import androidx.room.*

@Entity(tableName = "Surahs")
data class Surah(
    @PrimaryKey
    val id: Int,
    val name: String
)

@Entity(
    tableName = "Ayahs",
    foreignKeys = [
        ForeignKey(
            entity = Surah::class,
            parentColumns = ["id"],
            childColumns = ["surahId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("surahId")]
)
data class Ayah(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val ayahNumber: Int,
    val text: String,
    val surahId: Int
)

@Entity(
    tableName = "Tafsir",
    foreignKeys = [
        ForeignKey(
            entity = Ayah::class,
            parentColumns = ["id"],
            childColumns = ["ayahId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Tafsir(
    @PrimaryKey
    val ayahId: Int,
    val text: String
)

data class VerseWithTafsir(
    @ColumnInfo(name = "surahId") val surahId: Int,
    @ColumnInfo(name = "ayahNumber") val ayahNumber: Int,
    @ColumnInfo(name = "text") val text: String,
    @ColumnInfo(name = "tafsirText") val tafsirText: String
)

@Dao
interface QuranDao {
    @Query("SELECT * FROM Surahs")
    suspend fun getAllSurahs(): List<Surah>

    @Query("SELECT * FROM Surahs WHERE id = :id LIMIT 1")
    suspend fun getSurahById(id: Int): Surah?

    @Query("""
        SELECT a.surahId, a.ayahNumber, a.text, t.text AS tafsirText 
        FROM Ayahs a
        JOIN Tafsir t ON a.id = t.ayahId
        WHERE a.surahId = :surahId
        ORDER BY a.ayahNumber ASC
    """)
    suspend fun getVersesWithTafsir(surahId: Int): List<VerseWithTafsir>
}

@Database(entities = [Surah::class, Ayah::class, Tafsir::class], version = 1, exportSchema = false)
abstract class NoorQuranDatabase : RoomDatabase() {
    abstract fun quranDao(): QuranDao

    companion object {
        @Volatile
        private var INSTANCE: NoorQuranDatabase? = null

        fun getDatabase(context: android.content.Context): NoorQuranDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NoorQuranDatabase::class.java,
                    "noor_quran_database.db"
                )
                .createFromAsset("databases/noor_quran_database.db")
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
