package amu.wmi.eyelearner.app.auth.presentation.profile

//region imports
import amu.wmi.eyelearner.app.navigation.GenericMenu
import amu.wmi.eyelearner.app.navigation.LabeledInputField
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.koin.compose.viewmodel.koinViewModel
import androidx.compose.foundation.background
import androidx.compose.ui.BiasAlignment
import androidx.compose.foundation.layout.offset

//my imports
import amu.wmi.eyelearner.app.theme.LocalThemeManager

import amu.wmi.eyelearner.app.theme.Background
import amu.wmi.eyelearner.app.theme.ThemeManager
import amu.wmi.eyelearner.app.theme.CreatureBody
import amu.wmi.eyelearner.app.theme.DoubleEye

import amu.wmi.eyelearner.app.navigation.NavBar
import amu.wmi.eyelearner.app.navigation.NavigationData
import amu.wmi.eyelearner.app.navigation.MenuType
import amu.wmi.eyelearner.app.navigation.UserIconAndUserName
import amu.wmi.eyelearner.app.theme.MenuCreature
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.ui.unit.sp

//endregion




@Composable
fun HomeRoute(
    navData: NavigationData,
    onLoggedOut: () -> Unit = {},
    onNavigateToLogin: () -> Unit = onLoggedOut,
    viewModel: ProfileViewModel = koinViewModel())
{

    val state by viewModel.state.collectAsState()

    LaunchedEffect(state.isLoggedOut) {
        if (state.isLoggedOut) {
            onLoggedOut()
        }
    }

    HomeScreen(
        navData = navData,
        state = state,
        onIntent = viewModel::handleIntent,
        onNavigateToLogin = onNavigateToLogin
    )
}

@Composable
fun HomeScreen(
    navData: NavigationData,
    state: ProfileScreenState,
    onIntent: (ProfileIntent) -> Unit,
    onNavigateToLogin: () -> Unit = {}
) {


    Box(modifier = Modifier.fillMaxSize()) {
        val creature = MenuCreature(
            heightMin = 0.290f, heightMax = 0.3f,
            modifier = Modifier.align(BiasAlignment(horizontalBias = 4f / 6f, verticalBias = 1f)))

        GenericMenu(
            onIntent = onIntent,
            state = state,
            navData = navData,

            creature = { creature }
        ){}
    }

}



