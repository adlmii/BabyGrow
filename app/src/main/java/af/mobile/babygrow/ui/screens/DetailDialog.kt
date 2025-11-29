package af.mobile.babygrow.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
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

        val riskIcon = when(detail.riskLevel.uppercase()) {
            "HIGH" -> Icons.Outlined.WarningAmber
            "MEDIUM" -> Icons.Outlined.ElectricBolt
            else -> Icons.Outlined.CheckCircle
        }

        AlertDialog(
            onDismissRequest = { navController.popBackStack() },
            modifier = Modifier.fillMaxWidth(0.95f), // Dialog lebih lebar agar lega
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(28.dp), // Sudut membulat modern
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Detail Riwayat",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    IconButton(
                        onClick = { navController.popBackStack() },
                        colors = IconButtonDefaults.iconButtonColors(
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    ) {
                        Icon(Icons.Outlined.Close, contentDescription = "Close")
                    }
                }
            },
            text = {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // --- 1. Tanggal ---
                    item {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Outlined.CalendarToday,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                SimpleDateFormat("EEEE, dd MMM yyyy • HH:mm", Locale("id", "ID"))
                                    .format(detail.timestamp),
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // --- 2. Kartu Status Risiko (Modern) ---
                    item {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = riskColor.copy(alpha = 0.1f)
                            ),
                            shape = RoundedCornerShape(24.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(20.dp)
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                // Ikon dalam lingkaran
                                Box(
                                    modifier = Modifier
                                        .size(56.dp)
                                        .background(riskColor.copy(alpha = 0.2f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = riskIcon,
                                        contentDescription = null,
                                        tint = riskColor,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }

                                Column {
                                    Text(
                                        "Status Risiko",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        detail.riskLevel.uppercase(),
                                        style = MaterialTheme.typography.headlineSmall,
                                        fontWeight = FontWeight.Black,
                                        color = riskColor
                                    )
                                    Text(
                                        "Skor: ${detail.riskScore}",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold,
                                        color = riskColor.copy(alpha = 0.8f)
                                    )
                                }
                            }
                        }
                    }

                    // --- 3. Data Pemeriksaan (List dengan Icon) ---
                    if (input != null) {
                        item {
                            Text(
                                "Data Pemeriksaan",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(Modifier.height(12.dp))

                            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                DetailItem(
                                    icon = Icons.Outlined.Thermostat,
                                    label = "Suhu Tubuh",
                                    value = "${input.tempC ?: "-"} °C"
                                )
                                DetailItem(
                                    icon = Icons.Outlined.Sick,
                                    label = "Muntah",
                                    value = "${input.vomitCount} kali"
                                )
                                DetailItem(
                                    icon = Icons.Outlined.WaterDrop,
                                    label = "Popok Basah",
                                    value = "${input.wetDiaperCount} kali"
                                )
                                DetailItem(
                                    icon = Icons.Outlined.Restaurant,
                                    label = "Nafsu Makan",
                                    value = "${input.appetiteScore}/5"
                                )
                                DetailItem(
                                    icon = Icons.Outlined.Spa,
                                    label = "Kondisi BAB",
                                    value = "${input.stoolFreq}x (${input.stoolColor})"
                                )
                            }
                        }

                        // --- Gejala Tambahan ---
                        if (input.symptoms.isNotEmpty()) {
                            item {
                                Surface(
                                    color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(16.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                Icons.Outlined.WarningAmber,
                                                null,
                                                tint = MaterialTheme.colorScheme.error,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(Modifier.width(8.dp))
                                            Text(
                                                "Gejala Tambahan",
                                                style = MaterialTheme.typography.labelLarge,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.error
                                            )
                                        }
                                        Spacer(Modifier.height(8.dp))
                                        Text(
                                            input.symptoms.joinToString(", "),
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }
                    }

                    item { Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)) }

                    // --- 4. Rekomendasi ---
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Outlined.Lightbulb,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.tertiary
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    "Rekomendasi",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.tertiary
                                )
                            }
                            Text(
                                detail.shortRecommendation,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface,
                                lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.3
                            )
                        }
                    }
                }
            },
            confirmButton = {} // Tidak perlu tombol confirm
        )
    } else {
        LaunchedEffect(Unit) { navController.popBackStack() }
    }
}

@Composable
fun DetailItem(icon: ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(22.dp)
            )
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
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}