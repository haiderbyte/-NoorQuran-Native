package com.noor.quran

import androidx.compose.animation.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReaderScreen(
    surahId: Int,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val database = remember { NoorQuranDatabase.getDatabase(context) }
    val repository = remember { QuranRepository(database.quranDao()) }
    
    val viewModel: ReaderViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return ReaderViewModel(repository, surahId) as T
            }
        }
    )

    val state by viewModel.state.collectAsState()
    var isControlsVisible by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { isControlsVisible = !isControlsVisible }
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(top = 100.dp, bottom = 100.dp)
        ) {
            item {
                SurahIntroduction(state.surah)
            }
            items(state.verses, key = { "${it.surah_number}-${it.ayah_number}" }) { verse ->
                VerseCard(
                    verse = verse,
                    isExpanded = state.expandedAyah == verse.ayah_number,
                    onAyahClick = { viewModel.onAyahClick(verse.ayah_number) }
                )
            }
        }

        // Overlay Controls
        AnimatedVisibility(
            visible = isControlsVisible,
            enter = fadeIn() + slideInVertically { -it },
            exit = fadeOut() + slideOutVertically { -it }
        ) {
            TopAppBar(
                title = { 
                    Text(
                        state.surah?.nameArabic ?: "", 
                        color = Color.White, 
                        fontWeight = FontWeight.Light,
                        fontSize = 18.sp
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, "الخلف", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black.copy(alpha = 0.7f))
            )
        }
    }
}

@Composable
fun SurahIntroduction(surah: SurahEntity?) {
    if (surah == null) return
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = surah.nameArabic,
            color = Color.White,
            fontSize = 38.sp,
            fontWeight = FontWeight.ExtraLight
        )
        if (surah.id != 9) { // At-Tawbah does not start with Basmalah
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                color = Color(0xFFF8F5E9),
                fontSize = 24.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun VerseCard(
    verse: VerseEntity,
    isExpanded: Boolean,
    onAyahClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onAyahClick
            )
            .padding(horizontal = 24.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.End
    ) {
        Text(
            text = "${TextUtils.cleanQuranicText(verse.text)} ﴿${TextUtils.toArabicDigits(verse.ayah_number)}﴾",
            color = Color(0xFFF8F5E9),
            fontSize = 28.sp,
            lineHeight = 48.sp,
            textAlign = TextAlign.Right,
            modifier = Modifier.fillMaxWidth()
        )

        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Column(modifier = Modifier.padding(top = 16.dp)) {
                HorizontalDivider(color = Color(0xFF222222))
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = verse.tafsir_text,
                    color = Color(0xFF888888),
                    fontSize = 16.sp,
                    lineHeight = 24.sp,
                    textAlign = TextAlign.Right,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
