package amu.wmi.eyelearner.app.navigation

//region imports
import amu.wmi.eyelearner.app.theme.ThemeManager
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
//endregion


data class NavigationData(
    val onHome: () -> Unit,
    val onSearchStory: () -> Unit,
    val onMyStories: () -> Unit,
    val onCalendar: () -> Unit,
    val onProfile: () -> Unit,

    val homeText: String = "Dom",
    val searchStoriesText: String = "Znajdź fabuły",
    val myStoriesText: String = "Moje fabuły",
    val calendarText: String = "Kalendarz",
    val profileName: String = "User", // placeholder
)

enum class MenuType {
    home,
    searchStory,
    myStories,
    calendar,
    profile
}

@Composable
fun NavBar(
    navData: NavigationData,
    theme: ThemeManager,
    openedMenu: MenuType,

    navBarColor: Color,
    openedMenuColor: Color,
    otherMenuColor: Color,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(1f / 12f)          // ~1/12 of screen height
            .background(navBarColor)
            .padding(horizontal = 24.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // --- Text menu items ---
        NavItem(
            theme = theme,
            text = navData.homeText,
            selected = openedMenu == MenuType.home,
            openedMenuColor = openedMenuColor,
            otherMenuColor = otherMenuColor,
            onClick = navData.onHome
        )
        NavItem(
            theme = theme,
            text = navData.myStoriesText,
            selected = openedMenu == MenuType.myStories,
            openedMenuColor = openedMenuColor,
            otherMenuColor = otherMenuColor,
            onClick = navData.onMyStories
        )
        NavItem(
            theme = theme,
            text = navData.searchStoriesText,
            selected = openedMenu == MenuType.searchStory,
            openedMenuColor = openedMenuColor,
            otherMenuColor = otherMenuColor,
            onClick = navData.onSearchStory
        )
        NavItem(
            theme = theme,
            text = navData.calendarText,
            selected = openedMenu == MenuType.calendar,
            openedMenuColor = openedMenuColor,
            otherMenuColor = otherMenuColor,
            onClick = navData.onCalendar
        )

        // Push profile to the right
        Spacer(modifier = Modifier.weight(1f))

        // --- Profile section (dot + name) ---
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .clickable { navData.onProfile() }
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Round dot representing the user
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(
                        if (openedMenu == MenuType.profile) openedMenuColor
                        else otherMenuColor
                    )
            )
            // User name (placeholder)
            NavItem(
                theme = theme,
                text = navData.profileName,
                selected = openedMenu == MenuType.calendar,
                openedMenuColor = openedMenuColor,
                otherMenuColor = otherMenuColor,
                onClick = navData.onProfile
            )
        }
    }
}

    /**
     * A single text menu item rendered as a rounded "pill" when selected,
     * and as plain text otherwise.
     */
    @Composable
    private fun NavItem(
        theme: ThemeManager,
        text: String,
        selected: Boolean,
        openedMenuColor: Color,
        otherMenuColor: Color,
        onClick: () -> Unit,
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(50))       // pill / rounded background
                .clickable(onClick = onClick)
                .background(Color.Transparent)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                style = theme.getTitleFont(
                    color = if (selected) openedMenuColor else otherMenuColor,
                    fontSize = 20.sp,
                ),
                overflow = TextOverflow.Ellipsis
            )
        }
    }