package af.mobile.babygrow.data.api

import af.mobile.babygrow.data.model.Article
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET("articles")
    suspend fun getArticles(): List<Article>

    @GET("articles/{id}")
    suspend fun getArticleDetail(@Path("id") id: String): Article
}