package af.mobile.healthycheck.ui.features.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import af.mobile.healthycheck.ui.features.healthcheck.viewmodel.ArticleViewModel
import af.mobile.healthycheck.ui.navigation.Screen

@Composable
fun HomeScreen(
    navController: NavController,
    articleViewModel: ArticleViewModel = viewModel(),
    homeViewModel: HomeViewModel = viewModel()
) {
    val scrollState = rememberScrollState()

    // --- 1. State dari ViewModel (Logic) ---
    // Artikel
    val articles by articleViewModel.articles.collectAsState()
    val isLoading by articleViewModel.isLoading.collectAsState()
    val errorMessage by articleViewModel.errorMessage.collectAsState()

    // Home Logic
    val greetingState by homeViewModel.greetingState.collectAsState()

    // Fitur Data
    val featureList = homeViewModel.getFeatures(MaterialTheme.colorScheme.primary)

    // --- 2. Side Effects ---
    LaunchedEffect(Unit) {
        if (articles.isEmpty()) {
            articleViewModel.fetchArticles()
        }
    }

    // --- 3. Tampilkan UI  ---
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .verticalScroll(scrollState)
    ) {
        // Header
        HomeHeader(
            greetingText = greetingState.text,
            greetingIcon = greetingState.icon,
            onNotificationClick = { /* TODO: Notifikasi Logic */ }
        )

        // Banner
        HomeBanner(
            onClick = { navController.navigate(Screen.Input.route) }
        )

        // Content Surface
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = (-32).dp),
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 0.dp
        ) {
            Column(modifier = Modifier.padding(top = 32.dp)) {

                // Features Section
                FeaturesSection(
                    features = featureList,
                    onFeatureClick = { route -> navController.navigate(route) }
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Articles Section
                ArticlesSection(
                    articles = articles,
                    isLoading = isLoading,
                    errorMessage = errorMessage,
                    onViewAllClick = { navController.navigate(Screen.Articles.route) },
                    onArticleClick = { article ->
                        navController.currentBackStackEntry?.savedStateHandle?.set("selected_article", article)
                        navController.navigate(Screen.ArticleDetail.route)
                    }
                )

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}