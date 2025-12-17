package af.mobile.healthycheck.ui.features.healthcheck.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import af.mobile.healthycheck.data.repository.HealthRepository
import af.mobile.healthycheck.ui.model.HealthCheckInput
import af.mobile.healthycheck.ui.model.HealthCheckSummary
import af.mobile.healthycheck.ui.model.RiskLevel
import af.mobile.healthycheck.ui.features.healthcheck.util.ScoringEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

data class ResultUiState(
    val riskLevel: RiskLevel = RiskLevel.LOW,
    val riskScore: Int = 0,
    val reasons: List<String> = emptyList(),
    val recommendationShort: String = "Pantau kondisi anak",
    val saveStatus: String = ""
)

class ResultViewModel : ViewModel() {

    private val repository = HealthRepository()

    private val _uiState = MutableStateFlow(ResultUiState())
    val uiState: StateFlow<ResultUiState> = _uiState.asStateFlow()

    // Fungsi 1: untuk mendapatkan pesan error yang sesuai
    private fun getErrorMessage(e: Exception): String {
        return when (e) {
            is IOException -> "Gagal: Cek Koneksi Internet"
            is HttpException -> "Gagal: Masalah Server (${e.code()})"
            else -> "Gagal: Terjadi Kesalahan"
        }
    }

    // Fungsi 2: Evaluasi tanpa simpan
    fun evaluate(input: HealthCheckInput) {
        viewModelScope.launch {
            val result = ScoringEngine.evaluate(input)
            _uiState.value = ResultUiState(
                riskLevel = result.level,
                riskScore = result.score,
                reasons = result.reasons,
                recommendationShort = result.recommendation,
                saveStatus = ""
            )
        }
    }

    // Fungsi 3: Evaluasi dan Simpan
    fun evaluateAndSave(input: HealthCheckInput) {
        viewModelScope.launch {
            // 1. Hitung Skor
            val result = ScoringEngine.evaluate(input)

            _uiState.value = ResultUiState(
                riskLevel = result.level,
                riskScore = result.score,
                reasons = result.reasons,
                recommendationShort = result.recommendation,
                saveStatus = "Menyimpan Data..."
            )

            // 2. Siapkan Data
            val summary = HealthCheckSummary(
                timestamp = System.currentTimeMillis(),
                riskLevel = result.level.name,
                riskScore = result.score,
                shortRecommendation = result.recommendation,
                inputData = input
            )

            // 3. Simpan dengan Error Handling Spesifik
            try {
                repository.saveHealthCheck(summary)
                _uiState.value = _uiState.value.copy(saveStatus = "Berhasil Disimpan")
            } catch (e: Exception) {
                e.printStackTrace()
                val msg = getErrorMessage(e)
                _uiState.value = _uiState.value.copy(saveStatus = msg)
            }
        }
    }

    // Fungsi 4: Hapus Riwayat
    fun deleteHistory(id: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(saveStatus = "Menghapus...")
            try {
                repository.deleteAssessment(id)
                onSuccess()
            } catch (e: Exception) {
                e.printStackTrace()
                val msg = getErrorMessage(e)
                _uiState.value = _uiState.value.copy(saveStatus = msg)
            }
        }
    }
}