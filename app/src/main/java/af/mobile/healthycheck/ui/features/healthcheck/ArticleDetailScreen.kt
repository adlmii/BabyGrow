package af.mobile.healthycheck.ui.features.healthcheck

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.automirrored.outlined.Article
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import af.mobile.healthycheck.data.model.Article
import af.mobile.healthycheck.ui.navigation.Screen
import af.mobile.healthycheck.ui.features.healthcheck.viewmodel.ArticleViewModel
import androidx.compose.ui.text.AnnotatedString

@Composable
fun ArticleDetailScreen(
    navController: NavHostController,
    viewModel: ArticleViewModel = viewModel()
) {
    val initialArticle = navController.previousBackStackEntry?.savedStateHandle?.get<Article>("selected_article")
    val freshArticle by viewModel.selectedArticleData.collectAsState()
    val finalArticle = freshArticle ?: initialArticle

    val uriHandler = LocalUriHandler.current
    val scrollState = rememberScrollState()

    val allArticles by viewModel.articles.collectAsState()
    val relatedArticles = remember(finalArticle, allArticles) {
        if (finalArticle != null) {
            allArticles.filter { it.id != finalArticle.id }.shuffled().take(3)
        } else emptyList()
    }

    LaunchedEffect(initialArticle) {
        if (initialArticle != null) {
            viewModel.resetSelectedArticle()
            viewModel.fetchArticleDetail(initialArticle.id)
        }
        if (allArticles.isEmpty()) viewModel.fetchArticles()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        if (finalArticle != null) {

            // --- FIXED HEADER ---
            Surface(
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.primaryContainer
                                )
                            )
                        )
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.MenuBook,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.1f),
                        modifier = Modifier
                            .size(140.dp)
                            .align(Alignment.BottomEnd)
                            .offset(x = 30.dp, y = 30.dp)
                            .graphicsLayer { rotationZ = -15f }
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .windowInsetsPadding(WindowInsets.statusBars)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .padding(horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Kembali", tint = Color.White)
                            }
                            Text(
                                text = "Detail Edukasi",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                            IconButton(onClick = { /* Share */ }) {
                                Icon(Icons.Outlined.Share, contentDescription = "Share", tint = Color.White)
                            }
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 24.dp, end = 24.dp, bottom = 24.dp, top = 4.dp)
                        ) {
                            Surface(
                                color = Color.White.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    "INFO KESEHATAN",
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                            }
                            Spacer(Modifier.height(12.dp))
                            Text(
                                text = finalArticle.title,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White,
                                lineHeight = MaterialTheme.typography.headlineSmall.lineHeight * 1.2
                            )
                        }
                    }
                }
            }

            // --- SCROLLABLE CONTENT (ISI) ---
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(scrollState)
                    .background(MaterialTheme.colorScheme.background)
                    .padding(24.dp)
            ) {
                val primaryColor = MaterialTheme.colorScheme.primary
                val onSurfaceColor = MaterialTheme.colorScheme.onSurface
                val errorColor = MaterialTheme.colorScheme.error

                val styledContent = remember(finalArticle.content, primaryColor, onSurfaceColor, errorColor) {
                    formatArticleContent(finalArticle.content, primaryColor, onSurfaceColor, errorColor)
                }

                Text(
                    text = styledContent,
                    style = MaterialTheme.typography.bodyLarge,
                    lineHeight = 32.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Justify
                )

                Spacer(Modifier.height(40.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                Spacer(Modifier.height(24.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Outlined.Public,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "Sumber Referensi",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Spacer(Modifier.height(12.dp))
                        Button(
                            onClick = { uriHandler.openUri(finalArticle.sourceUrl) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = Color.White
                            )
                        ) {
                            Text("Buka Artikel Asli", fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(Modifier.height(48.dp))

                if (relatedArticles.isNotEmpty()) {
                    Text(
                        "Baca Juga",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(Modifier.height(16.dp))

                    relatedArticles.forEach { related ->
                        CompactArticleItem(
                            article = related,
                            onClick = {
                                navController.previousBackStackEntry?.savedStateHandle?.set("selected_article", related)
                                navController.navigate(Screen.ArticleDetail.route) {
                                    popUpTo(Screen.Articles.route) { inclusive = false }
                                }
                            }
                        )
                        Spacer(Modifier.height(12.dp))
                    }
                }
                Spacer(Modifier.height(20.dp))
            }
        } else {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
    }
}

@Composable
fun CompactArticleItem(article: Article, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.secondaryContainer,
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.Article,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
            Spacer(Modifier.width(16.dp))
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = article.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.Rounded.ChevronRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}

fun formatArticleContent(content: String, primaryColor: Color, textColor: Color, errorColor: Color): AnnotatedString {
    return buildAnnotatedString {
        val lines = content.split("\n")
        lines.forEachIndexed { index, line ->
            val trimmedLine = line.trim()
            when {
                trimmedLine.isNotEmpty() && trimmedLine.all { it.isUpperCase() || !it.isLetter() } && trimmedLine.length > 3 -> {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Black, color = primaryColor, fontSize = 16.sp, letterSpacing = 1.sp)) { append(line) }
                }
                Regex("^(\\d+\\.|•).*").containsMatchIn(trimmedLine) -> {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = textColor)) { append(line) }
                }
                trimmedLine.startsWith("CATATAN", ignoreCase = true) || trimmedLine.startsWith("NOTE", ignoreCase = true) -> {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = errorColor)) { append(line) }
                }
                else -> { append(line) }
            }
            if (index < lines.size - 1) { append("\n") }
        }
    }
}