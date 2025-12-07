package af.mobile.healthycheck.data.repository

import af.mobile.healthycheck.data.api.RetrofitClient
import af.mobile.healthycheck.data.model.Article
import af.mobile.healthycheck.ui.model.HealthCheckSummary

class HealthRepository {
    // Inisialisasi Retrofit API Client
    private val api = RetrofitClient.instance

    // 1. Ambil List Artikel
    suspend fun getArticles(): List<Article> {
        return api.getArticles()
    }

    // 2. Ambil Detail Artikel
    suspend fun getArticleDetail(index: Int): Article {
        return api.getArticleDetail(index)
    }

    // 3. Simpan Hasil Cek Kesehatan
    suspend fun saveHealthCheck(summary: HealthCheckSummary): Map<String, String> {
        return api.saveHealthCheck(summary)
    }

    // 4. Ambil Riwayat
    suspend fun getHealthHistory(limit: Int = 5, endAtId: String? = null): List<HealthCheckSummary> {
        // Menyiapkan parameter endAt
        val endAtParam = if (endAtId != null) "\"$endAtId\"" else null
        val responseMap = api.getHealthHistory(limit = limit, endAt = endAtParam)

        // Transformasi Map ke List
        val list = responseMap.map { (key, value) ->
            value.copy(id = key)
        }.sortedByDescending { it.timestamp }

        return if (endAtId != null) {
            list.filter { it.id != endAtId }
        } else {
            list
        }
    }

    // 5. Hapus Riwayat
    suspend fun deleteAssessment(id: String) {
        api.deleteAssessment(id)
    }
}