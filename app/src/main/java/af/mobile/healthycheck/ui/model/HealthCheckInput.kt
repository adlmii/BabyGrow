
package af.mobile.healthycheck.ui.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class HealthCheckInput(
    val gender: String,
    val ageMonths: Int,
    val tempC: Double?,
    val vomitCount: Int,
    val wetDiaperCount: Int,
    val appetiteScore: Int,
    val stoolFreq: Int,
    val stoolColor: String,
    val symptoms: List<String>
) : Parcelable