package af.mobile.healthycheck.ui.navigation

sealed class Screen(val route: String) {

    // --- HomeScreen ---
    data object Home : Screen("home")

    // Fitur 1: Analisis Pertumbuhan
    data object Growth : Screen("growth")

    // Fitur 2: Analisis Gizi
    data object Nutrition : Screen("nutrition")

    // Fitur 3: Health Check
    data object Input : Screen("input")
    data object Result : Screen("result")
    data object Articles : Screen("articles")
    data object ArticleDetail : Screen("article_detail")

    // Fitur 4: Risiko Stunting
    data object Stunting : Screen("stunting")
}