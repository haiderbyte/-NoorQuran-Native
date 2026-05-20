package com.noor.quran

import android.content.Context

class QuranRepository(context: Context) {
    private val dao = NoorQuranDatabase.getDatabase(context).quranDao()

    suspend fun getAllSurahs(): List<Surah> {
        return dao.getAllSurahs()
    }

    suspend fun getSurahById(id: Int): Surah? {
        return dao.getSurahById(id)
    }

    suspend fun getVersesForSurah(surahId: Int): List<VerseWithTafsir> {
        return dao.getVersesWithTafsir(surahId)
    }
}
