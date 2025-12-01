package af.mobile.babygrow

import af.mobile.babygrow.ui.screens.ArticleDetailScreen
import af.mobile.babygrow.ui.screens.ArticleScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import af.mobile.babygrow.ui.screens.InputScreen
import af.mobile.babygrow.ui.screens.ResultScreen
import af.mobile.babygrow.ui.theme.BabyGrowTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BabyGrowTheme {
                // Surface menggunakan background dari tema agar konsisten
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
    AppNavHost(navController)
}

@Composable
fun AppNavHost(navController: NavHostController) {
    // Start Destination tetap 'input'
    NavHost(navController = navController, startDestination = "input") {

        composable("input") {
            InputScreen(navController = navController)
        }

        composable("result") {
            ResultScreen(navController = navController)
        }

        composable("articles") {
            ArticleScreen(navController = navController)
        }

        composable("article_detail") {
            ArticleDetailScreen(navController = navController)
        }

    }
}