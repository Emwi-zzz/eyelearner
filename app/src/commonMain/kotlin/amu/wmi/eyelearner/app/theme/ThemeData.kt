package amu.wmi.eyelearner.app.theme

import androidx.compose.ui.graphics.Color

import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.TextUnit

data class AppTheme(
    val mainPanel_color: Color,
    val secondaryPanel_color: Color,
    val interactiblePanel_color: Color,

    val mainText_color: Color,
    val text_color: Color,
    val secondaryText_color: Color,
    val tertiaryText_color: Color,

    val buttonGreen: Color,
    val buttonRed: Color,

    val titleFontFamily: FontFamily = FontFamily.Default,
    val textFontFamily: FontFamily = FontFamily.Default,
)

data class CreatureTheme(
    val eye_color: Color,
    val body_color: Color,
    val backgroundBody_color: Color,
    val background_color: Color,
)


class ThemeManager {
    var appTheme: AppTheme by mutableStateOf(defaultAppTheme)
        private set

    var creatureTheme: CreatureTheme by mutableStateOf(defaultCreatureTheme)
        private set

    fun updateFonts(titleFont: FontFamily, textFont: FontFamily) {
        appTheme = appTheme.copy(
            titleFontFamily = titleFont,
            textFontFamily = textFont
        )
    }


    fun getTitleFont(
        color: Color = appTheme.mainText_color,
        fontSize: TextUnit  = 20.sp,
        fontWeight: FontWeight = FontWeight.SemiBold,
        fontStyle: FontStyle = FontStyle.Normal,
        letterSpacing: TextUnit  = 0.5.sp
    ) : TextStyle {
        return TextStyle(
            fontFamily = appTheme.titleFontFamily,
            fontSize = fontSize,
            fontWeight = fontWeight,
            fontStyle = fontStyle,
            letterSpacing = letterSpacing,
            color = color
        )
    }
}
    fun String.toComposeColor(): Color {
        val hex = removePrefix("#")
        val argb = when (hex.length) {
            6 -> "FF$hex"
            8 -> hex
            else -> error("Invalid color: $this")
        }
        return Color(argb.toLong(16))
    }

    private val defaultAppTheme = AppTheme(
        mainPanel_color = "#262626".toComposeColor(),
        secondaryPanel_color = "#313131".toComposeColor(),
        interactiblePanel_color = "#484848".toComposeColor(),

        mainText_color = "#ffffff".toComposeColor(),
        text_color = "#D9D9D9".toComposeColor(),
        secondaryText_color = "#AFAFAF".toComposeColor(),
        tertiaryText_color = "#848484".toComposeColor(),

        buttonGreen = "#48784D".toComposeColor(),
        buttonRed = "#48784D".toComposeColor(),

    )
    private val defaultCreatureTheme = CreatureTheme(
        eye_color = "#262626".toComposeColor(),
        body_color = "#933051".toComposeColor(),
        backgroundBody_color = "#3D0D1D".toComposeColor(),
        background_color = "#330917".toComposeColor(),
    )





