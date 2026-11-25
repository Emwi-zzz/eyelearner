package amu.wmi.eyelearner.app.navigation

//region imports
import amu.wmi.eyelearner.app.auth.presentation.profile.ProfileIntent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.compose.foundation.layout.height

import androidx.compose.foundation.text.BasicTextField
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp


//my imports
import amu.wmi.eyelearner.app.theme.ThemeManager
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.CardDefaults
import androidx.compose.ui.unit.TextUnit
//endregion


//region navbar
data class NavigationData(

//  Mena paska nawigacji
    val onHome: () -> Unit,
    val onSearchStory: () -> Unit,
    val onMyStories: () -> Unit,
    val onCalendar: () -> Unit,

    val homeText: String = "Dom",
    val searchStoriesText: String = "Znajdź fabuły",
    val myStoriesText: String = "Moje fabuły",
    val calendarText: String = "Kalendarz",
    val profileName: String = "User", // placeholder

//  Menu po rozszerzeniu paska nawigacji
    val onSettings: () -> Unit,
    val onProfile: () -> Unit,
    val onLoggedOut: () -> Unit,

    val profileText: String = "Profil",
    val settingsText: String = "Ustawienia",
    val logoutText: String = "Wyloguj",
)

enum class MenuType {
    home,
    searchStory,
    myStories,
    calendar,

    profile,
    settings,
}


@Composable
fun NavBar(
    onIntent: (ProfileIntent) -> Unit,

    navData: NavigationData,
    theme: ThemeManager,
    openedMenu: MenuType,

    openedMenuColor: Color,
    otherMenuColor: Color,
) {
    var profileMenuExpanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(1f / 12f)
            .background(theme.appTheme.mainPanel_color)
            .padding(horizontal = 24.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {

        EyeLearnerTextButton(
            theme = theme,
            text = navData.homeText,
            selected = openedMenu == MenuType.home,
            openedMenuColor = openedMenuColor,
            otherMenuColor = otherMenuColor,
            onClick = navData.onHome
        )
        EyeLearnerTextButton(
            theme = theme,
            text = navData.myStoriesText,
            selected = openedMenu == MenuType.myStories,
            openedMenuColor = openedMenuColor,
            otherMenuColor = otherMenuColor,
            onClick = navData.onMyStories
        )
        EyeLearnerTextButton(
            theme = theme,
            text = navData.searchStoriesText,
            selected = openedMenu == MenuType.searchStory,
            openedMenuColor = openedMenuColor,
            otherMenuColor = otherMenuColor,
            onClick = navData.onSearchStory
        )
        EyeLearnerTextButton(
            theme = theme,
            text = navData.calendarText,
            selected = openedMenu == MenuType.calendar,
            openedMenuColor = openedMenuColor,
            otherMenuColor = otherMenuColor,
            onClick = navData.onCalendar
        )

        Spacer(modifier = Modifier.weight(1f))

        Box {
            val profileInteraction = remember { MutableInteractionSource() }
            val profileHovered by profileInteraction.collectIsHoveredAsState()

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .clickable(
                        interactionSource = profileInteraction,
                        indication = null,
                        onClick = {
                            profileMenuExpanded = !profileMenuExpanded
                        }
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(
                            if (profileHovered || profileMenuExpanded) openedMenuColor
                            else otherMenuColor
                        )
                )

                Text(
                    text = navData.profileName,
                    color = if (profileHovered || profileMenuExpanded) openedMenuColor
                    else otherMenuColor,
                    style = theme.getTitleFont(fontSize = 20.sp),
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (profileMenuExpanded) {
                Popup(
                    alignment = Alignment.TopEnd,
                    offset = IntOffset(x = 6, y = -100),
                    onDismissRequest = { profileMenuExpanded = false },
                    properties = PopupProperties(focusable = true)

                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = theme.appTheme.secondaryPanel_color,
                        shadowElevation = 6.dp,
                        modifier = Modifier
                            .width(220.dp)
                            .padding(top = 8.dp)


                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            EyeLearnerTextButton(
                                theme = theme,
                                text = navData.profileText,
                                selected = openedMenu == MenuType.profile,
                                openedMenuColor = openedMenuColor,
                                otherMenuColor = otherMenuColor,
                                modifier = Modifier.fillMaxWidth(),
                                onClick = {
                                    profileMenuExpanded = false
                                    navData.onProfile()
                                }
                            )
                            EyeLearnerTextButton(
                                theme = theme,
                                text = navData.settingsText,
                                selected = openedMenu == MenuType.settings,
                                openedMenuColor = openedMenuColor,
                                otherMenuColor = otherMenuColor,
                                modifier = Modifier.fillMaxWidth(),
                                onClick = {
                                    profileMenuExpanded = false
                                    navData.onSettings()
                                }
                            )
                            EyeLearnerTextButton(
                                theme = theme,
                                text = navData.logoutText,
                                selected = false,
                                openedMenuColor = theme.appTheme.buttonRed,
                                otherMenuColor = theme.appTheme.buttonRed,
                                modifier = Modifier.fillMaxWidth(),
                                onClick = {
                                    profileMenuExpanded = false
                                    onIntent(ProfileIntent.Logout)
                                    navData.onLoggedOut()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}



    private fun Color.lighten(amount: Float): Color =
        lerp(this, Color.White, amount.coerceIn(0f, 1f))

    @Composable private fun EyeLearnerTextButton(
        theme: ThemeManager,
        text: String,
        selected: Boolean,
        openedMenuColor: Color,
        otherMenuColor: Color,
        modifier: Modifier = Modifier,
        onClick: () -> Unit,
    ) {
        val interactionSource = remember { MutableInteractionSource() }
        val hovered by interactionSource.collectIsHoveredAsState()

        val baseColor = if (selected) openedMenuColor else otherMenuColor
        val textColor = if (hovered) baseColor.lighten(0.25f) else baseColor

        Box(
            modifier = modifier
                .clip(RoundedCornerShape(50))
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onClick
                )
                .background(Color.Transparent)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                style = theme.getTitleFont(
                    color = textColor,
                    fontSize = 20.sp,
                ),
                overflow = TextOverflow.Ellipsis
            )
        }
    }
//endregion

//region underNavbar menu


//endregion

// region inpout field,   user icon and name
@Composable fun LabeledInputField(
    theme: ThemeManager,
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,

    labelWidth: Dp = 140.dp,
    fieldHeight: Dp = 48.dp,
    cornerRadius: Dp = 12.dp,
    placeholder: String = "",
    fontSize: TextUnit = 25.sp,
    singleLine: Boolean = true,
    enabled: Boolean = true,
    keyboardActions: androidx.compose.foundation.text.KeyboardActions =
        androidx.compose.foundation.text.KeyboardActions.Default,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = label,
            modifier = Modifier
                .width(labelWidth)
                .offset(y = 5.dp),
            style = theme.getTextFont(
                color = theme.appTheme.secondaryText_color,
                fontSize = fontSize,
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .height(fieldHeight)                    // ← height zamiast size()
                .clip(RoundedCornerShape(cornerRadius))
                .background(theme.appTheme.secondaryPanel_color)  // ← tło, opcjonalne
                .padding(horizontal = 12.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            if (value.isEmpty() && placeholder.isNotEmpty()) {
                Text(
                    text = placeholder,
                    style = theme.getTextFont(
                        color = theme.appTheme.text_color,
                        fontSize = fontSize,
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),                   // ← KLUCZ: wypełnia cały Box
                enabled = enabled,
                singleLine = singleLine,
                textStyle = theme.getTextFont(
                    color = theme.appTheme.text_color,
                    fontSize = fontSize,
                ),
                cursorBrush = SolidColor(theme.appTheme.text_color),
                keyboardActions = keyboardActions,
            )
        }
    }
}



@Composable fun UserIconAndUserName(
    theme: ThemeManager,
    userName: String,
    modifier: Modifier = Modifier,

    circleSize: Dp = 40.dp,
    fontSize: TextUnit = 40.sp,

    spacing: Dp = 60.dp,

) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(spacing)
    ) {
        Box(
            modifier = Modifier
                .size(circleSize)
                .clip(CircleShape)
                .background(theme.appTheme.text_color),
            contentAlignment = Alignment.Center
        ) {}

        Text(
            text = userName,
            style = theme.getTextFont(
                color = theme.appTheme.mainText_color,
                fontSize = fontSize,
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
//endregion


