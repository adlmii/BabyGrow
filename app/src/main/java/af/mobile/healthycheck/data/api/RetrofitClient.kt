package af.mobile.healthycheck.data.api

import af.mobile.healthycheck.BuildConfig
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    // Mengambil URL dari BuildConfig
    private const val BASE_URL = BuildConfig.BASE_URL

    val instance: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}