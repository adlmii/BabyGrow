package af.mobile.babygrow.ui.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class HealthCheckInput (
    val gender: String,          // "M" atau "F"
    val ageMonths: Int,          // usia anak (opsional, tapi direkomendasikan)
    val tempC: Double?,          // suhu
    val vomitCount: Int,         // muntah berapa kali
    val wetDiaperCount: Int,     // popok basah 24 jam
    val appetiteScore: Int,      // slider 1–5
    val stoolFreq: Int,          // frekuensi BAB
    val stoolColor: String,      // warna BAB
    val symptoms: List<String>   // gejala tambahan (batuk, pilek, sesak, dll)
) : Parcelable
