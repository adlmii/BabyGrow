package af.mobile.babygrow.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView

private val LightColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    onPrimary = NeutralWhite,
    primaryContainer = PrimaryBlueLight,
    onPrimaryContainer = PrimaryBlueDark,

    secondary = SecondaryPeach,
    onSecondary = NeutralWhite,
    secondaryContainer = SecondaryPeachLight,
    onSecondaryContainer = SecondaryPeachDark,

    tertiary = AccentMint,
    onTertiary = NeutralWhite,
    tertiaryContainer = AccentLavender,
    onTertiaryContainer = PrimaryBlueDark,

    background = SurfaceBackground,
    onBackground = NeutralGray900,

    surface = SurfaceCard,
    onSurface = NeutralGray900,
    surfaceVariant = NeutralGray100,
    onSurfaceVariant = NeutralGray700,

    error = StatusDanger,
    onError = NeutralWhite,
    errorContainer = StatusDangerLight,
    onErrorContainer = StatusDanger,

    outline = NeutralGray300,
    outlineVariant = NeutralGray200,
)

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryBlueLight,
    onPrimary = NeutralGray900,
    primaryContainer = PrimaryBlueDark,
    onPrimaryContainer = PrimaryBlueLight,

    secondary = SecondaryPeachLight,
    onSecondary = NeutralGray900,
    secondaryContainer = SecondaryPeachDark,
    onSecondaryContainer = SecondaryPeachLight,

    tertiary = AccentMint,
    onTertiary = NeutralGray900,
    tertiaryContainer = AccentLavender,
    onTertiaryContainer = NeutralWhite,

    background = NeutralGray900,
    onBackground = NeutralWhite,

    surface = NeutralGray800,
    onSurface = NeutralWhite,
    surfaceVariant = NeutralGray700,
    onSurfaceVariant = NeutralGray300,

    error = StatusDanger,
    onError = NeutralGray900,
    errorContainer = StatusDangerLight,
    onErrorContainer = StatusDanger,

    outline = NeutralGray600,
    outlineVariant = NeutralGray700,
)

@Composable
fun BabyGrowTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Disable dynamic color for consistent branding
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}