package af.mobile.babygrow.ui.util

import af.mobile.babygrow.ui.model.HealthCheckInput

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

        // --- Contoh rules sederhana berdasarkan IMCI ---
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

        // Warna BAB abnormal
        if (input.stoolColor == "Black" || input.stoolColor == "Bloody" || input.stoolColor == "Pale White") {
            score += 3
            reasons.add("Warna BAB tidak normal")
        }

        // Gejala tambahan
        if ("sesak" in input.symptoms) {
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
            "HIGH" -> "Segera kunjungi fasilitas kesehatan."
            "MEDIUM" -> "Pertimbangkan konsultasi dokter."
            else -> "Pantau kondisi anak dalam 24 jam."
        }

        return ScoringResult(
            score = score,
            level = level,
            reasons = reasons,
            recommendation = recommendation
        )
    }
}