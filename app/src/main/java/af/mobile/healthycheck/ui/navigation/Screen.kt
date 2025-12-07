package af.mobile.healthycheck.ui.navigation

sealed class Screen(val route: String) {

    // Screens
    data object Input : Screen("input")
    data object Result : Screen("result")
    data object Articles : Screen("articles")
    data object ArticleDetail : Screen("article_detail")
}