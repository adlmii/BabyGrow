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
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.Assignment
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import af.mobile.healthycheck.ui.features.healthcheck.model.HealthCheckInput
import af.mobile.healthycheck.ui.features.healthcheck.model.HealthCheckSummary
import af.mobile.healthycheck.ui.model.RiskLevel
import af.mobile.healthycheck.ui.theme.*
import af.mobile.healthycheck.ui.features.healthcheck.util.ScoringEngine
import af.mobile.healthycheck.ui.features.healthcheck.viewmodel.ResultViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(navController: NavHostController, vm: ResultViewModel = viewModel()) {
    // 1. Ambil Data dari Navigasi
    val input = navController.previousBackStackEntry?.savedStateHandle?.get<HealthCheckInput>("healthInput")
    val isHistoryView = navController.previousBackStackEntry?.savedStateHandle?.get<Boolean>("isHistoryView") ?: false

    // Ambil Data Summary Lengkap
    val summaryData = navController.previousBackStackEntry?.savedStateHandle?.get<HealthCheckSummary>("fullSummary")

    // 2. Logic Evaluasi
    LaunchedEffect(input) {
        if (input != null) {
            if (isHistoryView) {
                // Mode History
                vm.evaluate(input)
            } else {
                // Mode Input Baru
                vm.evaluateAndSave(input)
            }
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

    // --- HANDLE BACK BUTTON ---
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

            input?.let {
                navController.previousBackStackEntry?.savedStateHandle?.set(
                    "healthInput_${summary.timestamp}", it
                )
            }
        }
        navController.popBackStack()
    }

    BackHandler {
        handleBackButton()
    }

    // --- DIALOG KONFIRMASI HAPUS ---
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Hapus Riwayat?") },
            text = { Text("Data pemeriksaan ini akan dihapus permanen dari database.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        if (summaryData != null && summaryData.id.isNotEmpty()) {
                            vm.deleteHistory(summaryData.id) {
                                navController.popBackStack()
                            }
                        }
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = StatusDanger)
                ) {
                    Text("Hapus", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Batal")
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            textContentColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }

    // Animasi UI
    val animatedColor by animateColorAsState(
        targetValue = riskColor,
        animationSpec = tween(600), label = "ColorAnim"
    )

    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.8f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ), label = "ScaleAnim"
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        if (isHistoryView) "Detail Riwayat" else "Hasil Analisa",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { handleBackButton() }) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                    }
                },
                // Action Kanan: Tombol Hapus (Hanya muncul di Mode History)
                actions = {
                    if (isHistoryView) {
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = "Hapus Data",
                                tint = StatusDanger
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier.shadow(4.dp)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            contentPadding = PaddingValues(vertical = 24.dp)
        ) {
            // --- 1. KARTU UTAMA (RISK LEVEL) ---
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .scale(scale)
                        .shadow(12.dp, RoundedCornerShape(32.dp), spotColor = animatedColor),
                    shape = RoundedCornerShape(32.dp),
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
                                        animatedColor.copy(alpha = 0.15f),
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
                            verticalArrangement = Arrangement.spacedBy(20.dp)
                        ) {
                            // Icon Besar
                            Box(
                                modifier = Modifier
                                    .size(88.dp)
                                    .background(animatedColor.copy(alpha = 0.2f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = riskIcon,
                                    contentDescription = ui.riskLevel.name,
                                    tint = animatedColor,
                                    modifier = Modifier.size(48.dp)
                                )
                            }

                            // Teks Level & Warna Dinamis
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    "Tingkat Risiko",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    ui.riskLevel.name,
                                    style = MaterialTheme.typography.displayMedium,
                                    fontWeight = FontWeight.Black,
                                    color = animatedColor,
                                    textAlign = TextAlign.Center
                                )
                            }

                            // Badge Skor
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = animatedColor,
                                modifier = Modifier.shadow(4.dp, RoundedCornerShape(50))
                            ) {
                                Text(
                                    "Skor Risiko: ${ui.riskScore}",
                                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 10.dp),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.surface
                                )
                            }

                            // --- STATUS SIMPAN ---
                            if (ui.saveStatus.isNotEmpty()) {

                                val (statusIcon, statusColor, bgColor) = when {
                                    ui.saveStatus.contains("Berhasil") || ui.saveStatus.contains("Tersimpan") -> Triple(
                                        Icons.Rounded.CloudDone,
                                        StatusSuccess,
                                        StatusSuccess.copy(alpha = 0.1f)
                                    )
                                    ui.saveStatus.contains("Gagal") -> Triple(
                                        Icons.Rounded.CloudOff,
                                        StatusDanger,
                                        StatusDanger.copy(alpha = 0.1f)
                                    )
                                    ui.saveStatus.contains("Menghapus") -> Triple(
                                        Icons.Filled.Delete,
                                        StatusDanger,
                                        StatusDanger.copy(alpha = 0.1f)
                                    )
                                    else -> Triple(
                                        Icons.Rounded.CloudUpload,
                                        MaterialTheme.colorScheme.primary,
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = bgColor,
                                    border = BorderStroke(1.dp, statusColor.copy(alpha = 0.3f))
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                    ) {
                                        // Animasi Loading jika sedang proses
                                        if (ui.saveStatus.contains("Menyimpan") || ui.saveStatus.contains("Menghapus")) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(18.dp),
                                                strokeWidth = 2.dp,
                                                color = statusColor
                                            )
                                        } else {
                                            Icon(
                                                imageVector = statusIcon,
                                                contentDescription = null,
                                                modifier = Modifier.size(20.dp),
                                                tint = statusColor
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(
                                            text = ui.saveStatus,
                                            style = MaterialTheme.typography.labelLarge,
                                            fontWeight = FontWeight.SemiBold,
                                            color = statusColor
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // --- 2. KARTU REKOMENDASI ---
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = animatedColor.copy(alpha = 0.1f)
                    ),
                    border = BorderStroke(1.dp, animatedColor.copy(alpha = 0.3f))
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                Icons.Outlined.Lightbulb,
                                contentDescription = null,
                                tint = animatedColor,
                                modifier = Modifier.size(28.dp)
                            )
                            Text(
                                "Rekomendasi Medis",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Text(
                            ui.recommendationShort,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.4
                        )
                    }
                }
            }

            // --- 3. INPUT DATA SUMMARY ---
            if (input != null) {
                item {
                    InputDataCard(input)
                }
            }

            // --- 4. HEADER DETAIL PENILAIAN ---
            item {
                Text(
                    "Detail Penilaian",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            // --- 5. FAKTOR RISIKO & GEJALA ---
            item {
                AssessmentCard(
                    title = "Faktor Risiko Terdeteksi",
                    icon = Icons.Outlined.Analytics,
                    items = ui.reasons
                )
            }

            if (input?.symptoms?.isNotEmpty() == true) {
                item {
                    AssessmentCard(
                        title = "Gejala Dilaporkan",
                        icon = Icons.Outlined.MedicalServices,
                        items = input.symptoms
                    )
                }
            }

            // --- 6. TOMBOL AKSI BAWAH ---
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedButton(
                        onClick = { handleBackButton() },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                    ) {
                        Icon(Icons.Rounded.Home, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text(if (isHistoryView) "Kembali" else "Beranda", fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { /* Implementasi Share Nanti */ },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                            .shadow(4.dp, RoundedCornerShape(16.dp)),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(Icons.Rounded.Share, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Bagikan", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// --- KOMPONEN UI TAMBAHAN (Helper) ---

@Composable
fun InputDataCard(input: HealthCheckInput) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.AutoMirrored.Outlined.Assignment,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    "Data Pemeriksaan",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))

            // 1. Section Tanda Vital
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    "Tanda Vital",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        VitalItem(Icons.Outlined.Thermostat, "Suhu", "${input.tempC ?: "-"}°C")
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        VitalItem(Icons.Outlined.Sick, "Muntah", "${input.vomitCount}x")
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        VitalItem(Icons.Outlined.WaterDrop, "Popok", "${input.wetDiaperCount}x")
                    }
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))

            // 2. Section Lainnya
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                DataRowItem(
                    icon = Icons.Outlined.Restaurant,
                    label = "Nafsu Makan",
                    value = ScoringEngine.getAppetiteLabel(input.appetiteScore)
                )

                DataRowItem(
                    icon = Icons.Outlined.Spa,
                    label = "Pencernaan (BAB)",
                    value = "${input.stoolFreq}x sehari (${input.stoolColor})"
                )
            }
        }
    }
}

@Composable
fun DataRowItem(icon: ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
            modifier = Modifier.size(44.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(22.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
        Spacer(Modifier.width(16.dp))
        Column {
            Text(
                label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                value,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun VitalItem(icon: ImageVector, label: String, value: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(28.dp)
        )
        Spacer(Modifier.height(6.dp))
        Text(
            label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun AssessmentCard(
    title: String,
    icon: ImageVector,
    items: List<String>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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

            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)

            if (items.isEmpty()) {
                Text(
                    "Tidak ada data signifikan.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items.forEach { item ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                                modifier = Modifier.size(40.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = ScoringEngine.getRiskIcon(item),
                                        contentDescription = null,
                                        modifier = Modifier.size(20.dp),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                            Text(
                                item,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}