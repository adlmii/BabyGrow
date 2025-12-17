package af.mobile.healthycheck.ui.features.healthcheck.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class HealthCheckSummary(
    var id: String = "",
    val timestamp: Long,
    val riskLevel: String,
    val riskScore: Int,
    val shortRecommendation: String,
    val inputData: HealthCheckInput? = null
) : Parcelable