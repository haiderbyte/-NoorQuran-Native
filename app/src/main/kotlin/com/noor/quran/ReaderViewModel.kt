package com.noor.quran

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ReaderViewModel(
    application: Application,
    private val surahId: Int
) : AndroidViewModel(application) {

    private val repository = QuranRepository(application)
    private val bookmarkManager = BookmarkManager(application)

    private val _state = MutableStateFlow(ReaderState())
    val state: StateFlow<ReaderState> = _state.asStateFlow()

    init {
        loadSurah()
    }

    private fun loadSurah() {
        viewModelScope.launch {
            val surah = withContext(Dispatchers.IO) { repository.getSurahById(surahId) }
            _state.update { it.copy(surah = surah) }

            val verses = withContext(Dispatchers.IO) { repository.getVersesForSurah(surahId) }
            _state.update { it.copy(verses = verses, isLoading = false) }
        }
    }

    fun onAyahClick(ayahNumber: Int) {
        _state.update { 
            it.copy(expandedAyah = if (it.expandedAyah == ayahNumber) null else ayahNumber)
        }
        viewModelScope.launch {
            bookmarkManager.saveBookmark(surahId, ayahNumber)
        }
    }
}

data class ReaderState(
    val surah: Surah? = null,
    val verses: List<VerseWithTafsir> = emptyList(),
    val expandedAyah: Int? = null,
    val isLoading: Boolean = true
)
