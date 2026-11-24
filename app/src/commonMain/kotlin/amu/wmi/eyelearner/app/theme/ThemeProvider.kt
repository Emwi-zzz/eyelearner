package amu.wmi.eyelearner.app.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.runtime.LaunchedEffect
import eyelearner.app.generated.resources.Res
import eyelearner.app.generated.resources.allFontResources
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.Font
import org.jetbrains.compose.resources.Font


@Composable fun AppThemeProvider(themeManager: ThemeManager = GlobalThemeManager.instance, content: @Composable () -> Unit,) {

    // Use the exact file name without the .ttf extension
    val balooFontResource = Res.allFontResources["Baloo2_Bold"]
    val dongleFontResource = Res.allFontResources["Dongle_Bold"]

    val balooFontFamily = balooFontResource?.let {
        FontFamily(Font(it, FontWeight.Bold))
    } ?: FontFamily.Default

    val dongleFontFamily = dongleFontResource?.let {
        FontFamily(Font(it, FontWeight.Bold))
    } ?: FontFamily.Default

    LaunchedEffect(balooFontFamily, dongleFontFamily) {
        themeManager.updateFonts(
            titleFont = balooFontFamily,
            textFont = dongleFontFamily
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





