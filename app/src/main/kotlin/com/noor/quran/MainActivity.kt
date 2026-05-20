package com.noor.quran

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NoorQuranTheme {
                NoorQuranApp()
            }
        }
    }
}

@Composable
fun NoorQuranApp() {
    val navController = rememberNavController()
    
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.Black
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "surah_list",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("surah_list") {
                SurahListScreen(
                    onSurahClick = { surahId ->
                        navController.navigate("reader/$surahId")
                    }
                )
            }
            composable(
                route = "reader/{surahId}",
                arguments = listOf(navArgument("surahId") { type = NavType.IntType })
            ) { backStackEntry ->
                val surahId = backStackEntry.arguments?.getInt("surahId") ?: 1
                ReaderScreen(
                    surahId = surahId,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
