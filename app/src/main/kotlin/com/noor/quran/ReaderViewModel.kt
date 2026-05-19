package com.noor.quran

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ReaderViewModel(
    private val repository: QuranRepository,
    private val surahId: Int
) : ViewModel() {

    private val _state = MutableStateFlow(ReaderState())
    val state: StateFlow<ReaderState> = _state.asStateFlow()

    init {
        loadSurah()
    }

    private fun loadSurah() {
        viewModelScope.launch {
            val surah = repository.getSurahById(surahId)
            _state.update { it.copy(surah = surah) }
        }

        repository.getVersesForSurah(surahId)
            .onEach { verses ->
                _state.update { it.copy(verses = verses, isLoading = false) }
            }
            .launchIn(viewModelScope)
    }

    fun onAyahClick(ayahNumber: Int) {
        _state.update { 
            it.copy(expandedAyah = if (it.expandedAyah == ayahNumber) null else ayahNumber)
        }
        viewModelScope.launch {
            repository.saveBookmark(surahId, ayahNumber)
        }
    }
}

data class ReaderState(
    val surah: SurahEntity? = null,
    val verses: List<VerseEntity> = emptyList(),
    val expandedAyah: Int? = null,
    val isLoading: Boolean = true
)
