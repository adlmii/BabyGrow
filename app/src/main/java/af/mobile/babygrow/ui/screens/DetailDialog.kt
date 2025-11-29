package af.mobile.babygrow.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import af.mobile.babygrow.ui.model.HealthCheckInput
import af.mobile.babygrow.ui.model.HealthCheckSummary
import af.mobile.babygrow.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun DetailDialog(navController: NavHostController) {
    val detail = navController.previousBackStackEntry?.savedStateHandle?.get<HealthCheckSummary>("detailItem")
    val input = navController.previousBackStackEntry?.savedStateHandle?.get<HealthCheckInput>("detailInput")

    if (detail != null) {
        val riskColor = when(detail.riskLevel.uppercase()) {
            "HIGH" -> StatusDanger
            "MEDIUM" -> StatusWarning
            else -> StatusSuccess
        }

        AlertDialog(
            onDismissRequest = { navController.popBackStack() },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(24.dp),
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Detail Riwayat",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Outlined.Close, contentDescription = "Close")
                    }
                }
            },
            text = {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        DetailRow(
                            label = "Tanggal",
                            value = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID"))
                                .format(detail.timestamp)
                        )
                    }

                    item { Divider(color = MaterialTheme.colorScheme.outlineVariant) }

                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text(
                                "Status Risiko",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = riskColor.copy(alpha = 0.1f)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            "Level",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            detail.riskLevel,
                                            style = MaterialTheme.typography.titleLarge,
                                            fontWeight = FontWeight.Bold,
                                            color = riskColor
                                        )
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            "Skor",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            detail.riskScore.toString(),
                                            style = MaterialTheme.typography.titleLarge,
                                            fontWeight = FontWeight.Bold,
                                            color = riskColor
                                        )
                                    }
                                }
                            }
                        }
                    }

                    if (input != null) {
                        item { Divider(color = MaterialTheme.colorScheme.outlineVariant) }

                        item {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text(
                                    "Data Pemeriksaan",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )

                                DetailRow("Suhu", "${input.tempC ?: "-"} °C")
                                DetailRow("Muntah", "${input.vomitCount}x")
                                DetailRow("Popok Basah", "${input.wetDiaperCount}x")
                                DetailRow("Nafsu Makan", "${input.appetiteScore}/5")
                                DetailRow("BAB", "${input.stoolFreq}x, ${input.stoolColor}")

                                if (input.symptoms.isNotEmpty()) {
                                    Spacer(Modifier.height(4.dp))
                                    Text(
                                        "Gejala: ${input.symptoms.joinToString(", ")}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    item { Divider(color = MaterialTheme.colorScheme.outlineVariant) }

                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                "Rekomendasi",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                detail.shortRecommendation,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = MaterialTheme.typography.bodyMedium.lineHeight * 1.3
                            )
                        }
                    }
                }
            },
            confirmButton = {}
        )
    } else {
        LaunchedEffect(Unit) { navController.popBackStack() }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}