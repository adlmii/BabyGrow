package af.mobile.healthycheck.ui.components

import af.mobile.healthycheck.ui.navigation.Screen
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Analytics
import androidx.compose.material.icons.rounded.MonitorHeart
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.material.icons.rounded.Straighten
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

data class BottomNavItem(
    val title: String,
    val icon: ImageVector,
    val screen: Screen
)

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        // Fitur 1
        BottomNavItem("Tumbuh", Icons.Rounded.Straighten, Screen.Growth),
        // Fitur 2
        BottomNavItem("Gizi", Icons.Rounded.Restaurant, Screen.Nutrition),
        // Fitur 3 (Punya Kamu)
        BottomNavItem("Cek Yuk", Icons.Rounded.MonitorHeart, Screen.Input),
        // Fitur 4
        BottomNavItem("Stunting", Icons.Rounded.Analytics, Screen.Stunting)
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Navbar hanya muncul di 4 halaman utama ini
    val showBottomBar = items.any { it.screen.route == currentRoute }

    if (showBottomBar) {
        NavigationBar(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary,
            tonalElevation = 8.dp
        ) {
            items.forEach { item ->
                NavigationBarItem(
                    icon = { Icon(item.icon, contentDescription = item.title) },
                    label = { Text(item.title) },
                    selected = currentRoute == item.screen.route,
                    onClick = {
                        navController.navigate(item.screen.route) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    }
}