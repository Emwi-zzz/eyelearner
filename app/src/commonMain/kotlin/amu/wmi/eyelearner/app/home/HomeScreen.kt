package amu.wmi.eyelearner.app.auth.presentation.profile

//region imports
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

    val theme : ThemeManager = LocalThemeManager.current

    Background(
        backgroundColor =  theme.creatureTheme.background_color,
        squareColors = theme.creatureTheme.backgroundBody_color,

        modifier = Modifier
            .fillMaxSize()
            .background(color = theme.creatureTheme.background_color)
    ) {

        NavBar(
            onIntent = onIntent,

            theme = theme,
            navData = navData,
            openedMenu = MenuType.home,

            openedMenuColor = theme.appTheme.text_color,
            otherMenuColor = theme.appTheme.tertiaryText_color,
        )

        if(false){
            Card(
                modifier = Modifier
                    .widthIn(max = 480.dp)
                    .align(Alignment.Center)
                    .fillMaxWidth(),

                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {

                    if (state.session != null) {
                        Text(
                            text = "You are currently signed in.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 24.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Session Active",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                if (state.session.userId.isNotBlank()) {
                                    Text(
                                        text = "User ID: ${state.session.userId}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                }
                                Text(
                                    text = "Access Token: ${state.session.accessToken.take(12)}...",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        if (state.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(48.dp),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        else {
                            Button(
                                onClick = { onIntent(ProfileIntent.Logout) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.error
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                            ) {
                                Text(
                                    text = "Log Out",
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                        }
                    }
                    else {
    //                    Signed out

    //                    State is loading
                        if (state.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(48.dp),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
    //                    State log in
                        else onNavigateToLogin()
                    }
                }
            }
        }


        CreatureBody(
            theme = theme,

            heightMin = 0.290f,
            heightMax = 0.3f,

            topLeftCornerMin = 70.dp,
            topLeftCornerMax = 75.dp,
            topRightCornerMin = 60.dp,
            topRightCornerMax = 65.dp,

            width = 150.dp,

            modifier = Modifier.align(
                BiasAlignment(
                    horizontalBias = 4f / 6f,   // approximately 0.6667, corresponding to 5/6 position
                    verticalBias   = 1f         // 1f represents bottom
                )
            )
        ){
            Column(
                modifier = Modifier.fillMaxWidth().padding(32.dp) ,
                horizontalAlignment = Alignment.CenterHorizontally ,
                verticalArrangement = Arrangement.Center)
            {
                DoubleEye(
                    diameter = 60.dp,

                    insideColor = theme.creatureTheme.eye_color,

                    offsetX = -(4.dp),
                    offsetY = -(4.dp),

                    modifier = Modifier.offset(x = -(15.dp))

                )
            }
        }

    }
}



