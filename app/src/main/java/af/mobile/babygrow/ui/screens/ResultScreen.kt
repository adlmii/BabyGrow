package af.mobile.babygrow.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material.icons.outlined.ElectricBolt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import af.mobile.babygrow.ui.model.HealthCheckInput
import af.mobile.babygrow.ui.model.HealthCheckSummary
import af.mobile.babygrow.ui.theme.*
import af.mobile.babygrow.ui.viewmodel.ResultViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(navController: NavHostController, vm: ResultViewModel = viewModel()) {
    val input = navController.previousBackStackEntry?.savedStateHandle?.get<HealthCheckInput>("healthInput")

    LaunchedEffect(input) {
        input?.let { vm.evaluate(it) }
    }

    val ui by vm.uiState.collectAsState()

    val riskColor = when(ui.riskLevel.uppercase()) {
        "HIGH" -> StatusDanger
        "MEDIUM" -> StatusWarning
        else -> StatusSuccess
    }

    // Helper function untuk handle back button
    fun handleBackButton() {
        val summary = HealthCheckSummary(
            timestamp = System.currentTimeMillis(),
            riskLevel = ui.riskLevel,
            riskScore = ui.riskScore,
            shortRecommendation = ui.recommendationShort
        )
        navController.previousBackStackEntry?.savedStateHandle?.set("healthResult", summary)
        input?.let {
            navController.previousBackStackEntry?.savedStateHandle?.set(
                "healthInput_${summary.timestamp}", it
            )
        }
        navController.popBackStack()
    }

    val animatedColor by animateColorAsState(
        targetValue = riskColor,
        animationSpec = tween(600), label = ""
    )

    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.8f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ), label = ""
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Hasil Pemeriksaan",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        handleBackButton()
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier.shadow(2.dp)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            contentPadding = PaddingValues(vertical = 24.dp)
        ) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .scale(scale)
                        .shadow(8.dp, RoundedCornerShape(24.dp)),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        animatedColor.copy(alpha = 0.1f),
                                        MaterialTheme.colorScheme.surface
                                    )
                                )
                            )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Icon(
                                imageVector = when(ui.riskLevel.uppercase()) {
                                    "HIGH" -> Icons.Outlined.WarningAmber
                                    "MEDIUM" -> Icons.Outlined.ElectricBolt
                                    else -> Icons.Outlined.CheckCircle
                                },
                                contentDescription = ui.riskLevel,
                                tint = animatedColor,
                                modifier = Modifier.size(56.dp)
                            )

                            Text(
                                "Tingkat Risiko",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Text(
                                ui.riskLevel.uppercase(),
                                style = MaterialTheme.typography.displayMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = animatedColor
                            )

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = animatedColor.copy(alpha = 0.1f)
                            ) {
                                Text(
                                    "Skor: ${ui.riskScore}",
                                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = animatedColor
                                )
                            }
                        }
                    }
                }
            }

            item {
                AssessmentCard(
                    title = "Penilaian Kondisi",
                    icon = Icons.Outlined.Assessment,
                    reasons = ui.reasons,
                    symptoms = input?.symptoms ?: emptyList()
                )
            }

            item {
                RecommendationCard(
                    recommendation = ui.recommendationShort,
                    riskLevel = ui.riskLevel,
                    riskColor = riskColor
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = { handleBackButton() },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(Icons.Outlined.Home, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Kembali", fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { /* Share functionality */ },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 4.dp
                        )
                    ) {
                        Icon(Icons.Outlined.Share, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Bagikan", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun AssessmentCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    reasons: List<String>,
    symptoms: List<String>
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Divider(color = MaterialTheme.colorScheme.outlineVariant)

            if (reasons.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    reasons.forEach { reason ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                "•",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                reason,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            if (symptoms.isNotEmpty()) {
                Divider(color = MaterialTheme.colorScheme.outlineVariant)

                Text(
                    "Gejala Tambahan:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    symptoms.joinToString(", "),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun RecommendationCard(
    recommendation: String,
    riskLevel: String,
    riskColor: Color
) {
    val emoji = when(riskLevel.uppercase()) {
        "HIGH" -> "🏥"
        "MEDIUM" -> "👨‍⚕️"
        else -> "💚"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = riskColor.copy(alpha = 0.05f)
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    emoji,
                    style = MaterialTheme.typography.headlineMedium
                )
                Text(
                    "Rekomendasi",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = riskColor
                )
            }

            Text(
                recommendation,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.2
            )
        }
    }
}