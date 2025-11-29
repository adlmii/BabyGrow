package af.mobile.babygrow.ui.util

import af.mobile.babygrow.ui.model.HealthCheckInput
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector

data class ScoringResult(
    val score: Int,
    val level: String,
    val reasons: List<String>,
    val recommendation: String
)

object ScoringEngine {

    fun evaluate(input: HealthCheckInput): ScoringResult {
        var score = 0
        val reasons = mutableListOf<String>()

        // --- Rules Sederhana (IMCI / MTBS) ---
        // Suhu
        if (input.tempC != null && input.tempC >= 39.0) {
            score += 3
            reasons.add("Demam tinggi (≥39°C)")
        } else if (input.tempC != null && input.tempC >= 38.0) {
            score += 1
            reasons.add("Demam (≥38°C)")
        }

        // Muntah
        if (input.vomitCount >= 3) {
            score += 2
            reasons.add("Muntah berulang ≥3x")
        }

        // Popok basah (indikasi dehidrasi)
        if (input.wetDiaperCount <= 2) {
            score += 2
            reasons.add("Popok basah sedikit (risiko dehidrasi)")
        }

        // Nafsu makan rendah
        if (input.appetiteScore <= 2) {
            score += 2
            reasons.add("Nafsu makan menurun")
        }

        // Warna BAB abnormal (UPDATE: Cek Bahasa Indonesia)
        if (input.stoolColor == "Hitam" || input.stoolColor == "Berdarah" || input.stoolColor == "Putih Pucat") {
            score += 3
            reasons.add("Warna BAB tidak normal (${input.stoolColor})")
        }

        // Gejala tambahan
        if ("sesak" in input.symptoms.map { it.lowercase() } || input.symptoms.any { it.contains("Sesak", ignoreCase = true) }) {
            score += 3
            reasons.add("Gejala sesak napas")
        }

        // --- Risk Level ---
        val level = when {
            score >= 6 -> "HIGH"
            score >= 3 -> "MEDIUM"
            else -> "LOW"
        }

        // --- Rekomendasi ---
        val recommendation = when (level) {
            "HIGH" -> "Segera kunjungi fasilitas kesehatan terdekat (IGD/Puskesmas)."
            "MEDIUM" -> "Pertimbangkan konsultasi dokter dalam 24 jam."
            else -> "Kondisi stabil. Pantau terus dan berikan cairan yang cukup."
        }

        return ScoringResult(
            score = score,
            level = level,
            reasons = reasons,
            recommendation = recommendation
        )
    }

    // --- Helper Logic untuk UI ---

    fun getRiskIcon(text: String): ImageVector {
        val t = text.lowercase()
        return when {
            "demam" in t || "suhu" in t -> Icons.Outlined.Thermostat
            "muntah" in t -> Icons.Outlined.Sick
            "popok" in t || "basah" in t || "dehidrasi" in t -> Icons.Outlined.WaterDrop
            "makan" in t || "nafsu" in t -> Icons.Outlined.Restaurant
            "bab" in t || "diare" in t || "warna" in t -> Icons.Outlined.Spa
            "sesak" in t || "napas" in t -> Icons.Outlined.Air
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