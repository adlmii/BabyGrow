package af.mobile.healthycheck.ui.features.healthcheck.sections

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ExpandLess
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import af.mobile.healthycheck.ui.components.EmptyStateCard
import af.mobile.healthycheck.ui.components.HistoryCard
import af.mobile.healthycheck.ui.features.healthcheck.model.HealthCheckSummary

fun LazyListScope.historySection(
    history: List<HealthCheckSummary>,
    isLoading: Boolean,
    isLoadingMore: Boolean,
    isEndOfList: Boolean,
    onLoadMore: () -> Unit,
    onShowLess: () -> Unit,
    onItemClick: (HealthCheckSummary) -> Unit
) {
    // HEADER
    item {
        HorizontalDivider(modifier = Modifier.padding(top = 16.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
        Text(
            "Riwayat Pemeriksaan",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 24.dp, bottom = 4.dp)
        )
    }

    // CONTENT LIST
    if (isLoading) {
        item {
            Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
    } else if (history.isEmpty()) {
        item { EmptyStateCard() }
    } else {
        items(history) { item ->
            HistoryCard(item) { onItemClick(item) }
        }
    }

    // FOOTER BUTTONS
    if (history.isNotEmpty()) {
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (isLoadingMore) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                } else {
                    if (!isEndOfList) {
                        TextButton(onClick = onLoadMore) {
                            Icon(Icons.Outlined.ExpandMore, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Muat Lebih Banyak")
                        }
                    }

                    if (history.size > 5) {
                        TextButton(
                            onClick = onShowLess,
                            colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.secondary)
                        ) {
                            Icon(Icons.Outlined.ExpandLess, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Lihat Lebih Sedikit")
                        }
                    }
                }
            }
        }
    }
}