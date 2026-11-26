package amu.wmi.eyelearner.app.theme

import androidx.compose.ui.graphics.Color

import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
    private val defaultApp_DarkTheme = AppTheme(
        mainPanel_color = "#262626".toComposeColor(),
        secondaryPanel_color = "#313131".toComposeColor(),
        interactiblePanel_color = "#484848".toComposeColor(),

        mainText_color = "#ffffff".toComposeColor(),
        text_color = "#D9D9D9".toComposeColor(),
        secondaryText_color = "#AFAFAF".toComposeColor(),
        tertiaryText_color = "#848484".toComposeColor(),

        buttonGreen = "#48784D".toComposeColor(),
        buttonRed = "#D44141".toComposeColor(),

    )
    private val defaultApp_LightGreenTheme = AppTheme(
        mainPanel_color        = "#F4F7F2".toComposeColor(),
        secondaryPanel_color   = "#E7EEE4".toComposeColor(),
        interactiblePanel_color= "#D6E2D2".toComposeColor(),


        mainText_color         = "#1A221A".toComposeColor(),
        text_color             = "#2F3A2F".toComposeColor(),
        secondaryText_color    = "#5C6B5C".toComposeColor(),
        tertiaryText_color     = "#8A9A8A".toComposeColor(),


        buttonGreen            = "#3E7A45".toComposeColor(),
        buttonRed              = "#C0392B".toComposeColor(),


        titleFontFamily        = FontFamily.Default,
        textFontFamily         = FontFamily.Default,
    )
    private val defaultApp_LightGreyTheme = AppTheme(
            mainPanel_color         = "#F5F5F5".toComposeColor(),
            secondaryPanel_color    = "#E8E8E8".toComposeColor(),
            interactiblePanel_color = "#D6D6D6".toComposeColor(),

            mainText_color          = "#1A1A1A".toComposeColor(),
            text_color              = "#2E2E2E".toComposeColor(),
            secondaryText_color     = "#5C5C5C".toComposeColor(),
            tertiaryText_color      = "#8A8A8A".toComposeColor(),

            buttonGreen             = "#3E7A45".toComposeColor(),
            buttonRed               = "#C0392B".toComposeColor(),

            titleFontFamily         = FontFamily.Default,
            textFontFamily          = FontFamily.Default,
        )

data class CreatureTheme(
    val eye_color: Color,
    val body_color: Color,
    val backgroundBody_color: Color,
    val background_color: Color,
)
    private val defaultCreature_PinkTheme = CreatureTheme(
        eye_color = "#262626".toComposeColor(),
        body_color = "#933051".toComposeColor(),
        backgroundBody_color = "#3D0D1D".toComposeColor(),
        background_color = "#330917".toComposeColor(),
    )
    private val defaultCreature_LightGreenTheme = CreatureTheme(
        eye_color = "#1A221A".toComposeColor(),
        body_color = "#5FAE6B".toComposeColor(),
        backgroundBody_color = "#A8D5B0".toComposeColor(),
        background_color = "#DCEFE0".toComposeColor(),
    )
    private val defaultCreature_LightGreyTheme = CreatureTheme(
        eye_color            = "#1A1A1A".toComposeColor(),
        body_color           = "#9E9E9E".toComposeColor(),
        backgroundBody_color = "#CFCFCF".toComposeColor(),
        background_color     = "#EDEDED".toComposeColor(),
    )
    private val defaultCreature_DarkBlueTheme = CreatureTheme(
        eye_color            = "#0A1420".toComposeColor(),
        body_color           = "#3B6EA5".toComposeColor(),
        backgroundBody_color = "#1E3A5F".toComposeColor(),
        background_color     = "#0F1E33".toComposeColor(),
    )

data class ThemePack(
    val appTheme: AppTheme,
    val creatureTheme: CreatureTheme,
)
//
//    private val defaultThemePack = ThemePack(
//        appTheme = defaultApp_DarkTheme,
//        creatureTheme = defaultCreature_PinkTheme,
//    )

    private val defaultThemePack = ThemePack(
        appTheme = defaultApp_LightGreenTheme,
        creatureTheme = defaultCreature_LightGreenTheme,
    )


//    private val defaultThemePack = ThemePack(
//        appTheme = defaultApp_DarkTheme,
//        creatureTheme = defaultCreature_DarkBlueTheme,
//    )
//    private val defaultThemePack = ThemePack(
//        appTheme = defaultApp_LightGreyTheme,
//        creatureTheme = defaultCreature_LightGreyTheme,
//    )



class ThemeManager {
    var appTheme: AppTheme by mutableStateOf(defaultThemePack.appTheme)
        private set

    var creatureTheme: CreatureTheme by mutableStateOf(defaultThemePack.creatureTheme)
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

    fun getTextFont(
        color: Color = appTheme.mainText_color,
        fontSize: TextUnit  = 20.sp,
        fontWeight: FontWeight = FontWeight.SemiBold,
        fontStyle: FontStyle = FontStyle.Normal,
        letterSpacing: TextUnit  = 0.5.sp
    ) : TextStyle {
        return TextStyle(
            fontFamily = appTheme.textFontFamily,
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


