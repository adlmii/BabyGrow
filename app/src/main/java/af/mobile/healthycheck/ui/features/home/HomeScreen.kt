package af.mobile.healthycheck.ui.features.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import af.mobile.healthycheck.data.model.Article
import af.mobile.healthycheck.ui.features.healthcheck.viewmodel.ArticleViewModel
import af.mobile.healthycheck.ui.navigation.Screen
import androidx.compose.material3.MaterialTheme.colorScheme
import java.util.Calendar

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: ArticleViewModel = viewModel()
) {
    val scrollState = rememberScrollState()

    // --- State Data ---
    val articles by viewModel.articles.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    LaunchedEffect(Unit) {
        if (articles.isEmpty()) {
            viewModel.fetchArticles()
        }
    }

    // --- Logic Sapaan & Icon ---
    val (greetingText, greetingIcon) = remember {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        when (hour) {
            in 4..10 -> "Selamat Pagi" to Icons.Rounded.WbSunny
            in 11..14 -> "Selamat Siang" to Icons.Rounded.WbSunny
            in 15..18 -> "Selamat Sore" to Icons.Rounded.WbCloudy
            else -> "Selamat Malam" to Icons.Rounded.NightsStay
        }
    }

    // --- Data Fitur ---
    val featureList = listOf(
        FeatureData("Pertumbuhan", Icons.Rounded.Straighten, colorScheme.primary, Screen.Growth.route, rotation = -15f),
        FeatureData("Gizi", Icons.Rounded.Restaurant, colorScheme.primary, Screen.Nutrition.route),
        FeatureData("Stunting", Icons.Rounded.Analytics, colorScheme.primary, Screen.Stunting.route),
        FeatureData("Cek Yuk", Icons.Rounded.MonitorHeart, colorScheme.primary, Screen.Input.route)
    )

    // ROOT COLUMN
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.surface)
            .verticalScroll(scrollState)
    ) {

        // --- 1. HEADER ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(colorScheme.surface)
                .padding(top = 24.dp, bottom = 12.dp)
                .padding(horizontal = 24.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = greetingText,
                            style = MaterialTheme.typography.bodyMedium,
                            color = colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = greetingIcon,
                            contentDescription = null,
                            tint = colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Ayah & Bunda",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.onSurface
                    )
                }

                // --- Notifikasi ---
                IconButton(
                    onClick = { /* TODO: Notifikasi */ },
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(colorScheme.primary.copy(alpha = 0.1f))
                        .size(42.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Notifications,
                        contentDescription = "Notifikasi",
                        tint = colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }


        // --- 2. BANNER ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            colorScheme.primary,
                            colorScheme.primaryContainer
                        )
                    )
                )
                .clickable { navController.navigate(Screen.Input.route) }
        ) {
            Icon(
                imageVector = Icons.Rounded.MonitorHeart,
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .size(220.dp)
                    .offset(x = 50.dp, y = 40.dp)
                    .graphicsLayer { alpha = 0.15f },
                tint = Color.White
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(horizontal = 24.dp)
                    .padding(top = 48.dp, bottom = 80.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.White.copy(alpha = 0.2f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Prioritas",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Cek Kesehatan Si Kecil",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Deteksi dini gejala sakit sekarang >",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
        }

        // --- 3. CONTENT ---
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = (-32).dp),
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
            color = colorScheme.surface,
            shadowElevation = 0.dp
        ) {
            Column(
                modifier = Modifier.padding(top = 32.dp)
            ) {
                // A. FITUR
                Padding(horizontal = 24.dp) {
                    Text(
                        text = "Fitur",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(featureList) { feature ->
                        FeatureItemHorizontal(
                            data = feature,
                            textColor = colorScheme.onSurface,
                            onClick = { navController.navigate(feature.route) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // B. ARTIKEL
                Padding(horizontal = 24.dp) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Artikel Bunda",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.onSurface
                        )
                        Text(
                            text = "Lihat Semua",
                            style = MaterialTheme.typography.labelMedium,
                            color = colorScheme.primary,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.clickable { navController.navigate(Screen.Articles.route) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Column(
                    modifier = Modifier.padding(horizontal = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    when {
                        isLoading -> {
                            Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = colorScheme.primary)
                            }
                        }
                        errorMessage != null -> {
                            Text(
                                text = "Gagal memuat info.",
                                style = MaterialTheme.typography.bodySmall,
                                color = colorScheme.error,
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )
                        }
                        articles.isEmpty() -> {
                            Text(
                                text = "Belum ada artikel.",
                                style = MaterialTheme.typography.bodySmall,
                                color = colorScheme.onSurfaceVariant,
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )
                        }
                        else -> {
                            articles.take(3).forEach { article ->
                                ArticleListItem(
                                    article = article,
                                    onClick = {
                                        navController.currentBackStackEntry?.savedStateHandle?.set("selected_article", article)
                                        navController.navigate(Screen.ArticleDetail.route)
                                    }
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

// --- Komponen Pendukung ---

data class FeatureData(
    val title: String,
    val icon: ImageVector,
    val color: Color,
    val route: String,
    val rotation: Float = 0f
)

@Composable
fun FeatureItemHorizontal(
    data: FeatureData,
    textColor: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(76.dp)
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(data.color.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = data.icon,
                contentDescription = data.title,
                tint = data.color,
                modifier = Modifier
                    .size(30.dp)
                    .rotate(data.rotation)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = data.title,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Medium,
            color = textColor,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun ArticleListItem(
    article: Article,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .height(IntrinsicSize.Min)
                .padding(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(70.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(colorScheme.primaryContainer.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.Article,
                    contentDescription = null,
                    tint = colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = article.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = article.content,
                    style = MaterialTheme.typography.bodySmall,
                    color = colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun Padding(horizontal: androidx.compose.ui.unit.Dp, content: @Composable () -> Unit) {
    Box(modifier = Modifier.padding(horizontal = horizontal)) {
        content()
    }
}