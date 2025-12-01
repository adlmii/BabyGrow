package af.mobile.babygrow.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Article(
    val id: String,
    val title: String,

    @SerializedName("content")
    val content: String,

    @SerializedName("source_url")
    val sourceUrl: String
) : Parcelable