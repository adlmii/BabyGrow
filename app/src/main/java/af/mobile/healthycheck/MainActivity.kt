package af.mobile.healthycheck

import af.mobile.healthycheck.ui.navigation.Screen
import af.mobile.healthycheck.ui.screens.ArticleDetailScreen
import af.mobile.healthycheck.ui.screens.ArticleScreen
import af.mobile.healthycheck.ui.screens.InputScreen
import af.mobile.healthycheck.ui.screens.ResultScreen
import af.mobile.healthycheck.ui.theme.BabyGrowTheme
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

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

// Navigation Setup
@Composable
fun AppRoot() {
    val navController = rememberNavController()
    AppNavHost(navController)
}

// Navigation Host
@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.Input.route) {

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
    }
}