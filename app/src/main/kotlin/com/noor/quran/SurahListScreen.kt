package com.noor.quran

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SurahListScreen(
    onSurahClick: (Int) -> Unit
) {
    val context = LocalContext.current
    val database = remember { NoorQuranDatabase.getDatabase(context) }
    val repository = remember { QuranRepository(database.quranDao()) }
    val surahs by repository.getAllSurahs().collectAsState(initial = emptyList())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(48.dp))
        Text(
            text = "الفهرس",
            color = Color.White,
            fontSize = 32.sp,
            fontWeight = FontWeight.ExtraLight,
            modifier = Modifier.align(Alignment.End)
        )
        Spacer(modifier = Modifier.height(24.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(surahs, key = { it.id }) { surah ->
                SurahListItem(surah = surah, onClick = { onSurahClick(surah.id) })
            }
        }
    }
}

@Composable
fun SurahListItem(
    surah: SurahEntity,
    onClick: () -> Unit
) {
    Surface(
        color = Color.Transparent,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .padding(vertical = 16.dp, horizontal = 12.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = surah.nameArabic,
                    color = Color(0xFFF8F5E9),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Light
                )
                Text(
                    text = "${surah.revelationType} • ${surah.versesCount} آية",
                    color = Color(0xFF888888),
                    fontSize = 12.sp
                )
            }
            Spacer(modifier = Modifier.width(20.dp))
            Text(
                text = TextUtils.toArabicDigits(surah.id),
                color = Color(0xFF444444),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
