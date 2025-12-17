package af.mobile.healthycheck.ui.features.healthcheck.util

import af.mobile.healthycheck.ui.features.healthcheck.model.HealthCheckInput
import af.mobile.healthycheck.ui.model.RiskLevel
import af.mobile.healthycheck.ui.model.StoolColor
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector

data class ScoringResult(
    val score: Int,
    val level: RiskLevel,
    val reasons: List<String>,
    val recommendation: String
)

object ScoringEngine {

    fun evaluate(input: HealthCheckInput): ScoringResult {
        var score = 0
        val reasons = mutableListOf<String>()

        // --- 1. SUHU TUBUH  ---
        if (input.tempC != null) {
            // Kategori: BAYI (< 3 Bulan)
            if (input.ageMonths < 3) {
                if (input.tempC >= 38.0) {
                    score += 5
                    reasons.add("Demam pada bayi <3 bln adalah gawat darurat")
                } else if (input.tempC < 36.5) {
                    score += 3
                    reasons.add("Suhu tubuh terlalu rendah (Hipotermia)")
                }
            }
            // Kategori: BAYI & BALITA
            else {
                if (input.tempC >= 39.0) {
                    score += 3
                    reasons.add("Demam tinggi (≥39°C)")
                } else if (input.tempC >= 38.0) {
                    score += 1
                    reasons.add("Demam (≥38°C)")
                }
            }
        }

        // --- 2. MUNTAH ---
        if (input.vomitCount >= 3) {
            score += 2
            reasons.add("Muntah berulang ≥3x")
        }

        // --- 3. RISIKO DEHIDRASI ---
        if (input.wetDiaperCount <= 2) {
            val point = if (input.ageMonths < 6) 3 else 2
            score += point

            val reasonText = if (input.ageMonths < 6)
                "Popok kering (Bahaya dehidrasi pada bayi)"
            else
                "Buang air kecil sedikit (Indikasi dehidrasi)"

            reasons.add(reasonText)
        }

        // --- 4. NAFSU MAKAN ---
        if (input.appetiteScore <= 2) {
            score += 2
            reasons.add("Nafsu makan menurun drastis")
        }

        // --- 5. WARNA BAB ---
        val stoolEnum = StoolColor.fromLabel(input.stoolColor)
        if (stoolEnum == StoolColor.HITAM || stoolEnum == StoolColor.BERDARAH || stoolEnum == StoolColor.PUTIH_PUCAT) {
            score += 3
            reasons.add("Warna BAB berbahaya (${input.stoolColor})")
        }

        // --- 6. GEJALA TAMBAHAN ---
        if ("sesak" in input.symptoms.map { it.lowercase() } || input.symptoms.any { it.contains("Sesak", ignoreCase = true) }) {
            score += 3
            reasons.add("Gejala sesak napas")
        }

        // Kejang
        if (input.symptoms.any { it.contains("Kejang", ignoreCase = true) }) {
            score += 5
            reasons.add("Kejang Demam")
        }

        // --- HITUNG LEVEL RISIKO ---
        val level = when {
            score >= 6 -> RiskLevel.HIGH
            score >= 3 -> RiskLevel.MEDIUM
            else -> RiskLevel.LOW
        }

        // --- REKOMENDASI MEDIS ---
        val recommendation = when (level) {
            RiskLevel.HIGH -> "KONDISI GAWAT. Segera bawa anak ke IGD Rumah Sakit terdekat."
            RiskLevel.MEDIUM -> "Perlu perhatian medis. Konsultasikan ke dokter anak dalam 24 jam."
            else -> "Kondisi stabil. Pantau suhu tiap 4 jam dan pastikan anak cukup minum."
        }

        return ScoringResult(
            score = score,
            level = level,
            reasons = reasons,
            recommendation = recommendation
        )
    }

    // --- HELPER UTILS ---
    fun getRiskIcon(text: String): ImageVector {
        val t = text.lowercase()
        return when {
            "demam" in t || "suhu" in t || "hipotermia" in t -> Icons.Outlined.Thermostat
            "muntah" in t -> Icons.Outlined.Sick
            "popok" in t || "basah" in t || "dehidrasi" in t || "kecil" in t -> Icons.Outlined.WaterDrop
            "makan" in t || "nafsu" in t -> Icons.Outlined.Restaurant
            "bab" in t || "diare" in t || "warna" in t -> Icons.Outlined.Spa
            "sesak" in t || "napas" in t -> Icons.Outlined.Air
            "kejang" in t -> Icons.Outlined.Warning
            "batuk" in t -> Icons.Outlined.Masks
            "flu" in t || "pilek" in t -> Icons.Outlined.AcUnit
            "ruam" in t || "kulit" in t -> Icons.Outlined.Healing
            "menyusu" in t -> Icons.Outlined.ChildCare
            else -> Icons.Outlined.WarningAmber
        }
    }

    fun getAppetiteLabel(score: Int): String {
        return when(score) {
            1 -> "Tidak Mau Makan"
            2 -> "Kurang Nafsu"
            3 -> "Cukup / Biasa"
            4 -> "Lahap"
            5 -> "Sangat Lahap"
            else -> "$score/5"
        }
    }
}