package af.mobile.babygrow.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import af.mobile.babygrow.ui.model.HealthCheckInput
import af.mobile.babygrow.ui.util.ScoringEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ResultUiState(
    val riskLevel: String = "LOW",
    val riskScore: Int = 0,
    val reasons: List<String> = emptyList(),
    val recommendationShort: String = "Pantau kondisi anak"
)

class ResultViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ResultUiState())
    val uiState: StateFlow<ResultUiState> = _uiState.asStateFlow()

    fun evaluate(input: HealthCheckInput) {
        viewModelScope.launch {
            val result = ScoringEngine.evaluate(input)

            _uiState.value = ResultUiState(
                riskLevel = result.level,
                riskScore = result.score,
                reasons = result.reasons,
                recommendationShort = result.recommendation
            )
        }
    }
}