package com.self.lovenotes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.rememberPermissionState
import com.self.lovenotes.presentation.navigation.AppNavGraph
import com.self.lovenotes.presentation.theme.LoveNotesTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        enableEdgeToEdge()
        setContent {
            LoveNotesTheme {
                AppNavGraph(navController = rememberNavController())
            }
        }
    }
}