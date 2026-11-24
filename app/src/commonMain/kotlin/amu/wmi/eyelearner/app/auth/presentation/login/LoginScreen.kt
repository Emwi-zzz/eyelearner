package amu.wmi.eyelearner.app.auth.presentation.login

//region imports
import amu.wmi.eyelearner.app.core.platform.clearAuthCallbackParams
import amu.wmi.eyelearner.app.core.platform.getAuthCallbackParams
import amu.wmi.eyelearner.app.core.platform.getCurrentUrl
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.koin.compose.viewmodel.koinViewModel

import amu.wmi.eyelearner.app.theme.LocalThemeManager
import amu.wmi.eyelearner.app.theme.Creature
import amu.wmi.eyelearner.app.theme.DoubleEye
import amu.wmi.eyelearner.app.theme.ThemeManager
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ButtonDefaults
//endregion



@Composable fun LoginRoute(onNavigateToSignUp: () -> Unit = {}, onLoginSuccess: () -> Unit = {}, viewModel: LoginViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        val (token, verifier) = getAuthCallbackParams()
        if (!token.isNullOrBlank() && !verifier.isNullOrBlank()) {
            clearAuthCallbackParams()
            viewModel.handleIntent(LoginIntent.Finalize(token, verifier))
        }
    }

    LaunchedEffect(state.isLoginSuccessful) {
        if (state.isLoginSuccessful) {
            onLoginSuccess()
        }
    }

    LoginScreen(
        state = state,
        onIntent = viewModel::handleIntent,
        onNavigateToSignUp = onNavigateToSignUp
    )
}

@Composable fun LoginScreen(state: LoginScreenState, onIntent: (LoginIntent) -> Unit, onNavigateToSignUp: () -> Unit) {
    val theme : ThemeManager = LocalThemeManager.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = theme.creatureTheme.background_color)
        ,contentAlignment = Alignment.Center
    ) {
        LocalCreature(modifier = Modifier.align(Alignment.BottomCenter), theme = theme) {
            Column(modifier = Modifier.fillMaxWidth().padding(32.dp) ,horizontalAlignment = Alignment.CenterHorizontally ,verticalArrangement = Arrangement.Center) {

                DoubleEye(
                    diameter = 120.dp,
                    insideColor = theme.creatureTheme.eye_color,

                    offsetY = 20.dp
                )

                Spacer(modifier = Modifier.height(128.dp))

                when {
                    state.error != null -> LoginError(theme, state)
                    state.isLoading    -> LoginLoading(theme, state)
                    else               -> Login(theme, state, onIntent)
                }
            }
        }
    }





}



    //Composition elements
    @Composable fun LocalCreature(theme : ThemeManager, modifier : Modifier, content: @Composable () -> Unit) {
        Creature(modifier = modifier,
            theme = theme,

            topLeftCorner = 120.dp,
            topRightCorner = 120.dp,

            width = 480.dp,
            height = 0.7f,
        ){ content() }
    }


    //Login states
    @Composable fun LoginError(theme: ThemeManager, state: LoginScreenState){
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer
            ),
            modifier = Modifier
                .width(300.dp)
                .padding(bottom = 16.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = state.error ?: "",
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }

    @Composable fun LoginLoading(theme: ThemeManager, state: LoginScreenState){
        CircularProgressIndicator(
            modifier = Modifier.size(48.dp),
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Authenticating...",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }

    @Composable fun Login(theme: ThemeManager, state: LoginScreenState, onIntent: (LoginIntent) -> Unit){
        Button(
            onClick = {
                val currentUrl = getCurrentUrl()
                onIntent(LoginIntent.Login(currentUrl))
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = theme.appTheme.buttonGreen,
                contentColor = theme.appTheme.mainText_color
            )
        ) {
            Text(
                text = "Zaloguj się z USOS",
                style = MaterialTheme.typography.titleLarge
            )
        }
    }





