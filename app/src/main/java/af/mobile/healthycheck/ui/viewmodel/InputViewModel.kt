package af.mobile.healthycheck.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import af.mobile.healthycheck.data.api.RetrofitClient
import af.mobile.healthycheck.ui.model.HealthCheckSummary
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class InputViewModel : ViewModel() {

    // State untuk menampung list riwayat
    private val _history = MutableStateFlow<List<HealthCheckSummary>>(emptyList())
    val history: StateFlow<List<HealthCheckSummary>> = _history.asStateFlow()

    // State untuk indikator loading
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Init: Otomatis ambil data saat ViewModel dibuat
    init {
        fetchHistory()
    }

    fun fetchHistory() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // 1. Ambil data Map dari Firebase
                val responseMap = RetrofitClient.instance.getHealthHistory()

                // 2. Ubah Map menjadi List & Masukkan ID
                val listData = responseMap.map { (key, value) ->
                    value.copy(id = key)
                }.sortedByDescending { it.timestamp }

                _history.value = listData
            } catch (e: Exception) {
                e.printStackTrace()
                _history.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addHistory(summary: HealthCheckSummary) {
        _history.value = listOf(summary) + _history.value
    }
}