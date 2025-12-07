package af.mobile.healthycheck.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import af.mobile.healthycheck.data.api.RetrofitClient
import af.mobile.healthycheck.data.model.Article
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.IOException

class ArticleViewModel : ViewModel() {

    private val _articles = MutableStateFlow<List<Article>>(emptyList())
    val articles: StateFlow<List<Article>> = _articles.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _selectedArticleData = MutableStateFlow<Article?>(null)
    val selectedArticleData: StateFlow<Article?> = _selectedArticleData.asStateFlow()

    fun fetchArticles() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val response = RetrofitClient.instance.getArticles()

                _articles.value = response.filterNotNull()

            } catch (e: Exception) {
                if (e is IOException) {
                    _errorMessage.value = "Koneksi bermasalah. Cek internet kamu."
                } else {
                    _errorMessage.value = "Gagal memuat data: ${e.localizedMessage}"
                }
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Ambil detail (opsional, untuk melengkapi flow)
    fun fetchArticleDetail(id: String) {
        viewModelScope.launch {
            try {
                val index = id.toIntOrNull() ?: 0
                val data = RetrofitClient.instance.getArticleDetail(index)
                _selectedArticleData.value = data
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun resetSelectedArticle() {
        _selectedArticleData.value = null
    }
}