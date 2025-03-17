package com.self.lovenotes.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.self.lovenotes.ui.Calender.CalendarScreen

data class NavItem(val route: String, val iconImage: ImageVector?)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavGraph (navController: NavHostController) {
    val navItems = listOf(
        NavItem("Login", null),
        NavItem("Calender", Icons.Default.DateRange),
        NavItem("Journel", Icons.Default.FavoriteBorder),
        NavItem("DatePlanner", Icons.Default.Search),
        NavItem("Setting", Icons.Default.Settings),
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: "Calender"
    val currentNavItem = navItems.find { it.route == currentRoute } ?: navItems[0]

    Scaffold (
        modifier = Modifier.fillMaxSize(),
        topBar = {
            if (currentNavItem.route != "Login") {
                TopAppBar(
                    modifier = Modifier,
                    title = {
                        Text(
                            "LoveNotes",
                            fontWeight = FontWeight.SemiBold,
                            fontStyle = FontStyle.Italic,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                )
            }
        },
        bottomBar = {
            if (currentNavItem.route != "Login") {
                BottomAppBar {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.secondary),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        navItems.filter { it.iconImage != null }.forEach {
                            BottomAppBarButton(
                                modifier = Modifier,
                                image = it.iconImage!!,
                                name = it.route,
                                onClick = {
                                    navController.navigate(it.route) {
                                        // 백스택 관리
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true // 상태 저장
                                        }
                                        launchSingleTop = true // 중복 화면 방지
                                        restoreState = true // 상태 복원
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
                .padding(10.dp)
                .fillMaxSize()
        ){
            NavHost(navController = navController, startDestination = "Calender") {
                composable("Login") {
                    Text("Login")
                }
                composable("Calender") {
                    CalendarScreen()
                }
                composable("Journel") {
                    Text("Journel")
                }
                composable("DatePlanner") {
                    Text("DatePlanner")
                }
                composable("Setting") {
                    Text("Setting")
                }

            }
        }

    }
}

@Composable
fun BottomAppBarButton(modifier: Modifier, image: ImageVector, name: String, onClick: () -> Unit) {
    Row(
        modifier = modifier
            .widthIn(30.dp, 50.dp)
            .heightIn(30.dp, 50.dp)
            .clickable(true, onClick = onClick),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(-5.dp)
        ) {
            Icon(image, name, tint = MaterialTheme.colorScheme.primary)
            Text(text = name, fontSize = 6.sp)
        }
    }
}
