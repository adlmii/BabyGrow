package af.mobile.healthycheck.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import af.mobile.healthycheck.data.api.RetrofitClient
import af.mobile.healthycheck.data.model.Article
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.IOException // Import ini untuk mendeteksi error koneksi

class ArticleViewModel : ViewModel() {

    // --- STATE LIST ARTIKEL ---
    private val _articles = MutableStateFlow<List<Article>>(emptyList())
    val articles: StateFlow<List<Article>> = _articles.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // --- STATE DETAIL ARTIKEL ---
    private val _selectedArticleData = MutableStateFlow<Article?>(null)
    val selectedArticleData: StateFlow<Article?> = _selectedArticleData.asStateFlow()

    fun fetchArticles() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val response = RetrofitClient.instance.getArticles()
                _articles.value = response
            } catch (e: Exception) {
                // [BAGIAN INI YANG MENGUBAH PESAN ERROR]
                // Kita cek jenis errornya biar lebih pintar
                if (e is IOException) {
                    // Error Jaringan (Offline / DNS / Timeout)
                    _errorMessage.value = "Yah, gagal terhubung ke server. Periksa koneksi internetmu ya!"
                } else {
                    // Error Lainnya (Server Error / Format Data Salah)
                    _errorMessage.value = "Terjadi gangguan teknis. Coba lagi nanti ya."
                }

                // Print error asli di Logcat buat debugging (opsional)
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchArticleDetail(id: String) {
        viewModelScope.launch {
            try {
                val freshData = RetrofitClient.instance.getArticleDetail(id)
                _selectedArticleData.value = freshData
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun resetSelectedArticle() {
        _selectedArticleData.value = null
    }
}