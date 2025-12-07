package af.mobile.healthycheck.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import af.mobile.healthycheck.data.repository.HealthRepository
import af.mobile.healthycheck.data.model.Article
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class ArticleViewModel : ViewModel() {

    private val repository = HealthRepository()

    private val _articles = MutableStateFlow<List<Article>>(emptyList())
    val articles: StateFlow<List<Article>> = _articles.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _selectedArticleData = MutableStateFlow<Article?>(null)
    val selectedArticleData: StateFlow<Article?> = _selectedArticleData.asStateFlow()

    // Mengambil daftar artikel
    fun fetchArticles() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val response = repository.getArticles()
                _articles.value = response.filterNotNull()

            } catch (e: Exception) {
                e.printStackTrace()
                _errorMessage.value = when (e) {
                    is IOException -> "Tidak ada koneksi internet. Periksa wifi/data seluler kamu."
                    is HttpException -> "Gagal memuat artikel dari server (Kode: ${e.code()})."
                    else -> "Terjadi kesalahan: ${e.localizedMessage}"
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Mengambil detail artikel berdasarkan ID
    fun fetchArticleDetail(id: String) {
        viewModelScope.launch {
            _errorMessage.value = null
            try {
                val index = id.toIntOrNull() ?: 0
                val data = repository.getArticleDetail(index)
                _selectedArticleData.value = data
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Reset selected article data
    fun resetSelectedArticle() {
        _selectedArticleData.value = null
    }
}