package af.mobile.babygrow.ui.viewmodel

import af.mobile.babygrow.ui.model.HealthCheckSummary
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

class InputViewModel : ViewModel() {

    // Riwayat pemeriksaan (sementara, belum pakai DB)
    private val _history = MutableStateFlow<List<HealthCheckSummary>>(emptyList())
    val history: StateFlow<List<HealthCheckSummary>> = _history.asStateFlow()

    // Menambahkan riwayat baru (dipanggil ketika Route B mengembalikan hasil)
    suspend fun addHistory(summary: HealthCheckSummary) {
        withContext(Dispatchers.Main) {
            _history.value = listOf(summary) + _history.value
        }
    }
}