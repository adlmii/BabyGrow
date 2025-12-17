package af.mobile.healthycheck.ui.features.home

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import af.mobile.healthycheck.ui.navigation.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Calendar

data class HomeGreetingState(
    val text: String = "",
    val icon: ImageVector = Icons.Rounded.WbSunny
)

class HomeViewModel : ViewModel() {

    // --- State: Sapaan  ---
    private val _greetingState = MutableStateFlow(HomeGreetingState())
    val greetingState: StateFlow<HomeGreetingState> = _greetingState.asStateFlow()

    // --- State: List Fitur ---
    fun getFeatures(primaryColor: Color): List<FeatureData> {
        return listOf(
            FeatureData("Pertumbuhan", Icons.Rounded.Straighten, primaryColor, Screen.Growth.route, rotation = -15f),
            FeatureData("Gizi", Icons.Rounded.Restaurant, primaryColor, Screen.Nutrition.route),
            FeatureData("Stunting", Icons.Rounded.Analytics, primaryColor, Screen.Stunting.route),
            FeatureData("Cek Gejala", Icons.Rounded.MonitorHeart, primaryColor, Screen.Input.route)
        )
    }

    init {
        updateGreeting()
    }

    private fun updateGreeting() {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val (text, icon) = when (hour) {
            in 4..10 -> "Selamat Pagi" to Icons.Rounded.WbSunny
            in 11..14 -> "Selamat Siang" to Icons.Rounded.WbSunny
            in 15..18 -> "Selamat Sore" to Icons.Rounded.WbCloudy
            else -> "Selamat Malam" to Icons.Rounded.NightsStay
        }
        _greetingState.value = HomeGreetingState(text, icon)
    }
}