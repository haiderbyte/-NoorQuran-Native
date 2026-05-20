package com.noor.quran

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "bookmarks")

class BookmarkManager(private val context: Context) {
    private val LAST_READ_SURAH = intPreferencesKey("last_read_surah")
    private val LAST_READ_AYAH = intPreferencesKey("last_read_ayah")

    fun getLastRead(): Flow<Pair<Int, Int>?> {
        return context.dataStore.data.map { preferences ->
            val surah = preferences[LAST_READ_SURAH]
            val ayah = preferences[LAST_READ_AYAH]
            if (surah != null && ayah != null) {
                surah to ayah
            } else null
        }
    }

    suspend fun saveBookmark(surahId: Int, ayahId: Int) {
        context.dataStore.edit { preferences ->
            preferences[LAST_READ_SURAH] = surahId
            preferences[LAST_READ_AYAH] = ayahId
        }
    }
}
