package af.mobile.healthycheck.ui.features.healthcheck.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import af.mobile.healthycheck.data.repository.HealthRepository
import af.mobile.healthycheck.ui.model.HealthCheckSummary
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class InputViewModel : ViewModel() {

    private val repository = HealthRepository()
    private val LIMIT_PER_PAGE = 5

    private val _history = MutableStateFlow<List<HealthCheckSummary>>(emptyList())
    val history: StateFlow<List<HealthCheckSummary>> = _history.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isEndOfList = MutableStateFlow(false)
    val isEndOfList: StateFlow<Boolean> = _isEndOfList.asStateFlow()

    private val _isLoadingMore = MutableStateFlow(false)
    val isLoadingMore: StateFlow<Boolean> = _isLoadingMore.asStateFlow()

    init {
        fetchHistory()
    }

    fun fetchHistory() {
        viewModelScope.launch {
            _isLoading.value = true
            _isEndOfList.value = false
            try {
                val listData = repository.getHealthHistory(limit = LIMIT_PER_PAGE)
                _history.value = listData

                if (listData.size < LIMIT_PER_PAGE) {
                    _isEndOfList.value = true
                }

            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun showLessHistory() {
        fetchHistory()
    }

    fun loadMoreHistory() {
        if (_isLoadingMore.value || _isEndOfList.value || _history.value.isEmpty()) return

        viewModelScope.launch {
            _isLoadingMore.value = true
            try {
                val lastItem = _history.value.last()
                val lastId = lastItem.id

                val newItems = repository.getHealthHistory(limit = LIMIT_PER_PAGE, endAtId = lastId)

                if (newItems.isNotEmpty()) {
                    _history.value = _history.value + newItems

                    if (newItems.size < LIMIT_PER_PAGE) {
                        _isEndOfList.value = true
                    }
                } else {
                    _isEndOfList.value = true
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoadingMore.value = false
            }
        }
    }

    fun addHistory(summary: HealthCheckSummary) {
        _history.value = listOf(summary) + _history.value
    }
}