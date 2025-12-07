package af.mobile.healthycheck.data.api

import af.mobile.healthycheck.data.model.Article
import af.mobile.healthycheck.ui.model.HealthCheckSummary
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    // Article
    @GET("articles.json")
    suspend fun getArticles(): List<Article>

    @GET("articles/{index}.json")
    suspend fun getArticleDetail(@Path("index") index: Int): Article

    // Health Check Summary
    @POST("health_assessments.json")
    suspend fun saveHealthCheck(@Body summary: HealthCheckSummary): Map<String, String>

    @GET("health_assessments.json")
    suspend fun getHealthHistory(): Map<String, HealthCheckSummary>

    @DELETE("health_assessments/{id}.json")
    suspend fun deleteAssessment(@Path("id") id: String): retrofit2.Response<Void>
}