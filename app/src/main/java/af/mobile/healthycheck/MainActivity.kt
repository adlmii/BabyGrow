package af.mobile.healthycheck

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import af.mobile.healthycheck.ui.theme.BabyGrowTheme
import af.mobile.healthycheck.ui.navigation.Screen
import af.mobile.healthycheck.ui.components.BottomNavigationBar
import af.mobile.healthycheck.ui.features.healthcheck.InputScreen
import af.mobile.healthycheck.ui.features.healthcheck.ResultScreen
import af.mobile.healthycheck.ui.features.healthcheck.ArticleScreen
import af.mobile.healthycheck.ui.features.healthcheck.ArticleDetailScreen
import af.mobile.healthycheck.ui.features.home.HomeScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BabyGrowTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppRoot()
                }
            }
        }
    }
}

@Composable
fun AppRoot() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { BottomNavigationBar(navController = navController) }
    ) { innerPadding ->
        AppNavHost(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun AppNavHost(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier
    ) {

        // --- SCREEN BARU: HomeScreen ---
        composable(Screen.Home.route) {
            HomeScreen(navController = navController)
        }

        // --- FITUR 1: Pertumbuhan ---
        composable(Screen.Growth.route) {
            PlaceholderScreen("Fitur 1\nAnalisis Pertumbuhan")
        }

        // --- FITUR 2: Gizi ---
        composable(Screen.Nutrition.route) {
            PlaceholderScreen("Fitur 2\nAnalisis Gizi & Makan")
        }

        // --- FITUR 3: Health Check ---
        composable(Screen.Input.route) {
            InputScreen(navController = navController)
        }
        composable(Screen.Result.route) {
            ResultScreen(navController = navController)
        }
        composable(Screen.Articles.route) {
            ArticleScreen(navController = navController)
        }
        composable(Screen.ArticleDetail.route) {
            ArticleDetailScreen(navController = navController)
        }

        // --- FITUR 4: Stunting ---
        composable(Screen.Stunting.route) {
            PlaceholderScreen("Fitur 4\nRisiko Stunting")
        }
    }
}

@Composable
fun PlaceholderScreen(text: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}