package com.self.lovenotes.ui.navigation

import android.media.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

data class NavItem (val route: String, val iconImage: ImageVector, val itemType: String )

@Composable
fun AppNavGraph (navController: NavHostController) {
    val navItems = listOf(
        NavItem("Login", Icons.Default.Settings, "Full"),
        NavItem("Calender", Icons.Default.Home, "Branch"),
        NavItem("Journel", Icons.Default.DateRange, "Branch"),
        NavItem("DatePlanner", Icons.Default.Settings, "Branch"),
        NavItem("Setting", Icons.Default.Settings, "Branch"),
    )


    Scaffold (
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            BottomAppBar {
                Row (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 12.dp)
                ) {
                    navItems.filter { it.itemType == "Branch" }.forEach {
                        BottomAppBarButton (it.iconImage, it.route, {/* TODO */})
                    }
                }
            }
        }
    ){
        Column (
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
        ){
            NavHost(navController = navController, startDestination = "Login") {
                composable("Login") {
                    Text("Login")
                }
                composable("Calender") {
                    Text("Calender")
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
fun BottomAppBarButton (image: ImageVector, name: String, onClick: () -> Unit) {
    Column (

    ){
        Button(onClick = onClick) {
            Icon(image, name)
            Text(text = name)
        }
    }


}