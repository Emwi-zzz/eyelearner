package amu.wmi.eyelearner.app.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.runtime.LaunchedEffect
import com.composables.googlefonts.rememberGoogleFontFamily

@Composable fun AppThemeProvider(themeManager: ThemeManager = GlobalThemeManager.instance, content: @Composable () -> Unit,) {

    val baloo = rememberGoogleFontFamily("Baloo 2", 400, 700)
    val dongle = rememberGoogleFontFamily("Dongle", 400, 700)

    // 2. Update the ThemeManager when the fonts load
    LaunchedEffect(baloo, dongle) {
        themeManager.updateFonts(
            titleFont = baloo,
            textFont = dongle
        )
    }

    CompositionLocalProvider(LocalThemeManager provides themeManager) {
        content()
    }
}

val LocalThemeManager = staticCompositionLocalOf<ThemeManager> {
    error("ThemeManager not provided. Wrap your root composable in AppThemeProvider.")
}

object GlobalThemeManager {
    val instance: ThemeManager by lazy { ThemeManager() }
}





