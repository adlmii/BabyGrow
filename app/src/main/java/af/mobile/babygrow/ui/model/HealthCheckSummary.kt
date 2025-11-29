package af.mobile.babygrow.ui.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class HealthCheckSummary (
    val timestamp: Long,            // waktu pemeriksaan
    val riskLevel: String,          // "LOW" / "MEDIUM" / "HIGH"
    val riskScore: Int,             // skor numerik
    val shortRecommendation: String, // rekomendasi singkat
    val inputData: HealthCheckInput? = null
) : Parcelable