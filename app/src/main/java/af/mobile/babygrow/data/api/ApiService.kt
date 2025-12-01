package af.mobile.babygrow.data.api

import af.mobile.babygrow.data.model.Article
import retrofit2.http.GET

interface ApiService {
    @GET("articles")
    suspend fun getArticles(): List<Article>
}