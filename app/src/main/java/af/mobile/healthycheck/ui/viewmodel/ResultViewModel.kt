package af.mobile.healthycheck.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import af.mobile.healthycheck.data.api.RetrofitClient
import af.mobile.healthycheck.ui.model.HealthCheckInput
import af.mobile.healthycheck.ui.model.HealthCheckSummary
import af.mobile.healthycheck.ui.util.ScoringEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ResultUiState(
    val riskLevel: String = "LOW",
    val riskScore: Int = 0,
    val reasons: List<String> = emptyList(),
    val recommendationShort: String = "Pantau kondisi anak",
    val saveStatus: String = ""
)

class ResultViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ResultUiState())
    val uiState: StateFlow<ResultUiState> = _uiState.asStateFlow()

    // Fungsi 1: Hanya Evaluasi (Untuk Mode History)
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

    // Fungsi 2: Evaluasi & Simpan (Untuk Input Baru)
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
                riskLevel = result.level,
                riskScore = result.score,
                shortRecommendation = result.recommendation,
                inputData = input
            )

            // 3. Kirim ke Firebase
            try {
                RetrofitClient.instance.saveHealthCheck(summary)

                _uiState.value = _uiState.value.copy(
                    saveStatus = "Berhasil Disimpan"
                )
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.value = _uiState.value.copy(
                    saveStatus = "Gagal Terhubung"
                )
            }
        }
    }

    fun deleteHistory(id: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(saveStatus = "Menghapus...")
            try {
                RetrofitClient.instance.deleteAssessment(id)
                onSuccess()
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.value = _uiState.value.copy(saveStatus = "Gagal Menghapus")
            }
        }
    }
}