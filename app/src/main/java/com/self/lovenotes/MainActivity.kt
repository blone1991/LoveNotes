package com.self.lovenotes

import android.app.ComponentCaller
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.rememberPermissionState
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.self.lovenotes.presentation.navigation.AppNavGraph
import com.self.lovenotes.presentation.theme.LoveNotesTheme
import dagger.hilt.android.AndroidEntryPoint
import java.util.Timer

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LoveNotesTheme {
                val navController = rememberNavController()
                AppNavGraph(navController = navController)
            }
        }
    }

    override fun onNewIntent(intent: Intent, caller: ComponentCaller) {
        super.onNewIntent(intent, caller)
        setIntent(intent)
    }
}

