package com.self.lovenotes.presentation.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.self.lovenotes.presentation.calendar.CalendarScreen
import com.self.lovenotes.presentation.login.LoginScreen
import com.self.lovenotes.presentation.memory.TrackingResultScreen
import com.self.lovenotes.presentation.memory.view.DateMemoryScreen
import com.self.lovenotes.presentation.planner.GeneratedPlanScreen
import com.self.lovenotes.presentation.planner.PlannerScreen
import com.self.lovenotes.presentation.setting.SettingScreen

data class NavItem(val route: String, val iconImage: ImageVector?)

val LocalSnackbarHostState = compositionLocalOf<SnackbarHostState> { error("") }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavGraph(
    navController: NavHostController,
) {
    val navItems = listOf(
        NavItem("Login", null),
        NavItem("Calendar", Icons.Default.DateRange),
        NavItem("Memory", Icons.Default.FavoriteBorder),
//        NavItem("Journel", Icons.Default.FavoriteBorder),
        NavItem("Planner", Icons.Default.Search),
        NavItem("Setting", Icons.Default.Settings),
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: navItems[0].route
    val currentNavItem = navItems.find { it.route == currentRoute } ?: navItems[0]

    val snackbarHostState = remember { SnackbarHostState() }
    val startDestination = remember { mutableStateOf("Login") }

    CompositionLocalProvider(value = LocalSnackbarHostState.provides(snackbarHostState)) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            snackbarHost = {
                SnackbarHost(
                    hostState = snackbarHostState,
                )
            },
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "LoveNotes",
                            style = MaterialTheme.typography.headlineLarge.copy(fontStyle = FontStyle.Italic),
                            color = MaterialTheme.colorScheme.primary,
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors()
                        .copy(containerColor = MaterialTheme.colorScheme.background)
                )
            },
            bottomBar = {
                if (currentNavItem.route != "Login") {
                    BottomAppBar(
                        modifier = Modifier.fillMaxWidth(),
                        containerColor = Color.DarkGray.copy(alpha = 0.8f),
                        tonalElevation = 8.dp
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            navItems.filter { it.iconImage != null }.forEach { item ->
                                BottomAppBarButton(
                                    modifiner = Modifier
                                        .weight(1f)
                                        .fillMaxWidth(),
                                    currentRoute = currentRoute,
                                    item = item,
                                    onClick = {
                                        navController.navigate(item.route) {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        ) {
            Column(
                modifier = Modifier
                    .padding(it)
                    .then(other = Modifier)
                    .padding(horizontal = 16.dp)
                    .fillMaxSize()
            ) {
                NavHost(
                    navController = navController,
                    startDestination = startDestination.value
                ) {
                    composable("Login") {
                        LoginScreen(onLogin = { startDestination.value = "Calendar" })
                    }
                    composable("Calendar") {
                        CalendarScreen()
                    }
                    composable("Journel") {
                        Text("Journel")
                    }
                    composable("Planner") {
                        PlannerScreen(navController = navController)
                    }
                    composable("GeneratedPlanScreen") { backStackEntry ->
                        GeneratedPlanScreen(
                            onDismiss = { navController.popBackStack() } // 뒤로 가기
                        )
                    }
                    composable("Setting") {
                        SettingScreen()
                    }

                    composable("TrackingResult") {
                        TrackingResultScreen(onSave = { navController.navigate("Memory") })
//                        TrackingResultScreen(onSave = { navController.popBackStack() })
                    }

                    composable("Memory") {
                        DateMemoryScreen(navController = navController)
                    }
                }
            }
        }
    }
}


@Composable
fun BottomAppBarButton(
    modifiner: Modifier,
    currentRoute: String,
    item: NavItem,
    onClick: () -> Unit,
) {
    Row(
        modifier = modifiner,
        horizontalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = onClick,
            colors = ButtonDefaults.buttonColors().copy(containerColor = Color.Transparent)
        ) {
            Column(
                modifier = Modifier,
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = item.iconImage!!,
                    contentDescription = item.route,
                    tint = if (item.route == currentRoute) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = item.route,
                    color = if (item.route == currentRoute) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                    fontSize = 8.sp
                )
            }
        }
    }
}
