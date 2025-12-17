package af.mobile.healthycheck.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material.icons.outlined.ElectricBolt
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import af.mobile.healthycheck.ui.features.healthcheck.model.HealthCheckSummary
import af.mobile.healthycheck.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun HistoryCard(
    item: HealthCheckSummary,
    onClick: () -> Unit
) {
    val riskColor = when(item.riskLevel.uppercase()) {
        "HIGH" -> StatusDanger
        "MEDIUM" -> StatusWarning
        else -> StatusSuccess
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(riskColor.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = when(item.riskLevel.uppercase()) {
                            "HIGH" -> Icons.Outlined.WarningAmber
                            "MEDIUM" -> Icons.Outlined.ElectricBolt
                            else -> Icons.Outlined.CheckCircle
                        },
                        contentDescription = item.riskLevel,
                        tint = riskColor,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Column {
                    Text(
                        SimpleDateFormat("dd MMM yyyy", Locale("id", "ID")).format(item.timestamp),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        SimpleDateFormat("HH:mm", Locale("id", "ID")).format(item.timestamp),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = riskColor.copy(alpha = 0.1f)
            ) {
                Text(
                    item.riskLevel,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = riskColor
                )
            }
        }
    }
}