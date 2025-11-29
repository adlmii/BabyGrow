package af.mobile.babygrow.ui.viewmodel

import androidx.lifecycle.ViewModel
import af.mobile.babygrow.ui.model.HealthCheckSummary
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class InputViewModel : ViewModel() {

    private val _history = MutableStateFlow<List<HealthCheckSummary>>(emptyList())
    val history: StateFlow<List<HealthCheckSummary>> = _history.asStateFlow()

    suspend fun addHistory(summary: HealthCheckSummary) {
        _history.value = listOf(summary) + _history.value
    }
}