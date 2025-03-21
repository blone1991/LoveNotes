package com.self.lovenotes.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.self.lovenotes.ui.Calendar.CalendarScreen
import com.self.lovenotes.ui.Planner.PlannerScreen
import com.self.lovenotes.ui.Setting.SettingScreen

data class NavItem(val route: String, val iconImage: ImageVector?)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavGraph (navController: NavHostController) {
    val navItems = listOf(
        NavItem("Calendar", Icons.Default.DateRange),
//        NavItem("Journel", Icons.Default.FavoriteBorder),
        NavItem("DatePlanner", Icons.Default.Search),
        NavItem("Setting", Icons.Default.Settings),
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: navItems[0].route
    val currentNavItem = navItems.find { it.route == currentRoute } ?: navItems[0]

    Scaffold (
        modifier = Modifier.fillMaxSize(),
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
            if (currentNavItem.route != "Auth") {
                BottomAppBar {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Gray),
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
    ){
        Column (
            modifier = Modifier
                .padding(it)
                .then(other = Modifier)
                .padding(16.dp)
                .fillMaxSize()
        ){
            NavHost(
                navController = navController,
                startDestination = "Calendar"
            ) {
                composable("Calendar") {
                    CalendarScreen()
                }
                composable("Journel") {
                    Text("Journel")
                }
                composable("DatePlanner") {
                    PlannerScreen()
                }
                composable("Setting") {
                    SettingScreen()
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
