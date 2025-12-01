package af.mobile.babygrow.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import af.mobile.babygrow.data.api.RetrofitClient
import af.mobile.babygrow.data.model.Article
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ArticleViewModel : ViewModel() {

    // Menyimpan daftar artikel
    private val _articles = MutableStateFlow<List<Article>>(emptyList())
    val articles: StateFlow<List<Article>> = _articles.asStateFlow()

    // Menyimpan status loading (sedang memuat atau tidak)
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Menyimpan pesan error jika gagal
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // Fungsi yang dipanggil UI untuk minta data
    fun fetchArticles() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null // Reset error

            try {
                // Proses ambil data di background
                val response = RetrofitClient.instance.getArticles()
                _articles.value = response
            } catch (e: Exception) {
                _errorMessage.value = "Gagal memuat: ${e.message}"
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
}