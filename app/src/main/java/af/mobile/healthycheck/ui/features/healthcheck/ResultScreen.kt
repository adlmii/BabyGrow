package af.mobile.healthycheck.ui.features.healthcheck

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController

// --- Imports ---
import af.mobile.healthycheck.ui.features.healthcheck.model.HealthCheckInput
import af.mobile.healthycheck.ui.features.healthcheck.model.HealthCheckSummary
import af.mobile.healthycheck.ui.model.RiskLevel
import af.mobile.healthycheck.ui.theme.*
import af.mobile.healthycheck.ui.features.healthcheck.viewmodel.ResultViewModel
import af.mobile.healthycheck.ui.components.SimpleHeader

@Composable
fun ResultScreen(
    navController: NavHostController,
    vm: ResultViewModel = viewModel()
) {
    // --- 1. DATA & LOGIC ---
    val input = navController.previousBackStackEntry?.savedStateHandle?.get<HealthCheckInput>("healthInput")
    val isHistoryView = navController.previousBackStackEntry?.savedStateHandle?.get<Boolean>("isHistoryView") ?: false
    val summaryData = navController.previousBackStackEntry?.savedStateHandle?.get<HealthCheckSummary>("fullSummary")

    LaunchedEffect(input) {
        if (input != null) {
            if (isHistoryView) vm.evaluate(input) else vm.evaluateAndSave(input)
        }
    }

    val ui by vm.uiState.collectAsState()

    val riskColor = when(ui.riskLevel) {
        RiskLevel.HIGH -> StatusDanger
        RiskLevel.MEDIUM -> StatusWarning
        else -> StatusSuccess
    }
    val riskIcon = when(ui.riskLevel) {
        RiskLevel.HIGH -> Icons.Outlined.WarningAmber
        RiskLevel.MEDIUM -> Icons.Outlined.ElectricBolt
        else -> Icons.Outlined.CheckCircle
    }

    fun handleBackButton() {
        if (!isHistoryView) {
            val summary = HealthCheckSummary(
                timestamp = System.currentTimeMillis(),
                riskLevel = ui.riskLevel.name,
                riskScore = ui.riskScore,
                shortRecommendation = ui.recommendationShort,
                inputData = input
            )
            navController.previousBackStackEntry?.savedStateHandle?.set("healthResult", summary)
        }
        navController.popBackStack()
    }

    BackHandler { handleBackButton() }

    var showDeleteDialog by remember { mutableStateOf(false) }
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Hapus Riwayat?", style = MaterialTheme.typography.titleMedium) },
            text = { Text("Data ini akan dihapus permanen.", style = MaterialTheme.typography.bodyMedium) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        if (summaryData != null && summaryData.id.isNotEmpty()) {
                            vm.deleteHistory(summaryData.id) { navController.popBackStack() }
                        }
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = StatusDanger)
                ) { Text("Hapus", fontWeight = FontWeight.Bold) }
            },
            dismissButton = { TextButton(onClick = { showDeleteDialog = false }) { Text("Batal") } },
            containerColor = MaterialTheme.colorScheme.surface
        )
    }

    // Animasi
    val animatedColor by animateColorAsState(targetValue = riskColor, animationSpec = tween(600), label = "Color")
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }
    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.8f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow), label = "Scale"
    )

    // --- LAYOUT ---
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        SimpleHeader(
            title = if (isHistoryView) "Detail Riwayat" else "Hasil Analisa",
            onBackClick = { handleBackButton() },
            actionIcon = if (isHistoryView) Icons.Filled.Delete else null,
            onActionClick = if (isHistoryView) { { showDeleteDialog = true } } else null,
            actionColor = StatusDanger
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            contentPadding = PaddingValues(top = 12.dp, bottom = 32.dp)
        ) {

            // CARD 1: RISIKO
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .scale(scale)
                        .shadow(12.dp, RoundedCornerShape(32.dp), spotColor = animatedColor),
                    shape = RoundedCornerShape(32.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Brush.verticalGradient(colors = listOf(animatedColor.copy(alpha = 0.15f), MaterialTheme.colorScheme.surface)))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 28.dp, horizontal = 24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(80.dp)
                                    .background(animatedColor.copy(alpha = 0.2f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(imageVector = riskIcon, contentDescription = null, tint = animatedColor, modifier = Modifier.size(40.dp))
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Tingkat Risiko", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(
                                    ui.riskLevel.name,
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = animatedColor,
                                    textAlign = TextAlign.Center
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = animatedColor,
                                modifier = Modifier.shadow(4.dp, RoundedCornerShape(50))
                            ) {
                                Text(
                                    "Skor Risiko: ${ui.riskScore}",
                                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.surface
                                )
                            }
                        }
                    }
                }
            }

            // CARD 2: REKOMENDASI
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = animatedColor.copy(alpha = 0.08f)),
                    border = BorderStroke(1.dp, animatedColor.copy(alpha = 0.3f))
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Icon(Icons.Outlined.Lightbulb, contentDescription = null, tint = animatedColor, modifier = Modifier.size(24.dp))
                            Text("Rekomendasi Medis", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                        }
                        Text(
                            ui.recommendationShort,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = MaterialTheme.typography.bodyMedium.lineHeight * 1.3
                        )
                    }
                }
            }

            // CARD 3: INPUT DATA
            if (input != null) {
                item { InputDataCard(input) }
            }

            // LIST FAKTOR RISIKO
            item {
                Text(
                    "Detail Penilaian",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            item {
                AssessmentCard(title = "Faktor Risiko Terdeteksi", icon = Icons.Outlined.Analytics, items = ui.reasons)
            }

            // LIST GEJALA
            if (input?.symptoms?.isNotEmpty() == true) {
                item {
                    AssessmentCard(title = "Gejala Dilaporkan", icon = Icons.Outlined.MedicalServices, items = input.symptoms)
                }
            }

            // BUTTONS
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    OutlinedButton(
                        onClick = { handleBackButton() },
                        modifier = Modifier.weight(1f).height(50.dp),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                    ) {
                        Icon(Icons.Rounded.Home, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(if (isHistoryView) "Kembali" else "Beranda", fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { /* Share Logic */ },
                        modifier = Modifier.weight(1f).height(50.dp).shadow(2.dp, RoundedCornerShape(14.dp)),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(Icons.Rounded.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Bagikan", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}