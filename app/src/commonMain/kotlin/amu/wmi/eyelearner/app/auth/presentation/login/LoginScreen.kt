package amu.wmi.eyelearner.app.auth.presentation.login


//region imports
import amu.wmi.eyelearner.app.core.platform.clearAuthCallbackParams
import amu.wmi.eyelearner.app.core.platform.getAuthCallbackParams
import amu.wmi.eyelearner.app.core.platform.getCurrentUrl
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ButtonDefaults
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path

//My imports
import amu.wmi.eyelearner.app.theme.LocalThemeManager
import amu.wmi.eyelearner.app.theme.CreatureBody
import amu.wmi.eyelearner.app.theme.DoubleEye
import amu.wmi.eyelearner.app.theme.Background
import amu.wmi.eyelearner.app.theme.ThemeManager
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

    Background(
        backgroundColor =  theme.creatureTheme.background_color,
        squareColors = theme.creatureTheme.backgroundBody_color,

        modifier = Modifier
            .fillMaxSize()
            .background(color = theme.creatureTheme.background_color)
    ){

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
        CreatureBody(modifier = modifier,
            theme = theme,

            topLeftCornerMax = 183.dp,
            topLeftCornerMin = 165.dp,
            topRightCornerMax = 163.dp,
            topRightCornerMin = 145.dp,

            width = 480.dp,
            heightMax = 0.7f,
            heightMin = 0.69f,
        ){ content() }
    }


    //Login states
    @Composable fun LoginError(theme: ThemeManager, state: LoginScreenState){
        Card(
            colors = CardDefaults.cardColors(
                containerColor = theme.appTheme.buttonRed,
                contentColor = theme.appTheme.mainText_color
            ),
            modifier = Modifier
                .width(300.dp)
                .padding(bottom = 16.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = state.error ?: "",
                    style = theme.getTitleFont(),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }

    @Composable fun LoginLoading(theme: ThemeManager, state: LoginScreenState){
        CircularProgressIndicator(
            modifier = Modifier.size(48.dp),
            color = theme.appTheme.mainText_color,
        )

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Authenticating...",
            style = theme.getTitleFont(theme.appTheme.mainText_color)
        )
    }

    @Composable fun Login(theme: ThemeManager, state: LoginScreenState, onIntent: (LoginIntent) -> Unit) {
        Button(
            onClick = {
                val currentUrl = getCurrentUrl()
                onIntent(LoginIntent.Login(currentUrl))
            },
            modifier = Modifier
                .width(400.dp)
                .height(80.dp)
                .drawWithContent {
                    drawContent()

                    val radius     = 30.dp.toPx()
                    val handWidth  = 50.dp.toPx()
                    val handHeight = 45.dp.toPx()
                    val offset     = handWidth
                    val color      = theme.creatureTheme.body_color

                    drawTopRoundedRect(
                        color   = color,
                        topLeft = Offset(offset, size.height - handHeight),
                        width   = handWidth,
                        height  = handHeight,
                        radius  = radius
                    )

                    drawTopRoundedRect(
                        color   = color,
                        topLeft = Offset(size.width - handWidth - offset, size.height - handHeight),
                        width   = handWidth,
                        height  = handHeight,
                        radius  = radius
                    )
                },

            colors = ButtonDefaults.buttonColors(
                containerColor = theme.appTheme.buttonGreen,
                contentColor = theme.appTheme.mainText_color
            ),
            contentPadding = PaddingValues(0.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(ButtonDefaults.shape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Zaloguj się z USOS",
                    style = theme.getTitleFont()
                )
            }
        }
    }

        private fun DrawScope.drawTopRoundedRect(color: Color, topLeft: Offset, width: Float, height: Float, radius: Float) {
            val l = topLeft.x
            val t = topLeft.y
            val r = l + width
            val b = t + height


            val path = Path().apply {
                moveTo(l, t + radius)
                quadraticTo(l, t, l + radius, t)         // top-left round
                lineTo(r - radius, t)
                quadraticTo(r, t, r, t + radius)         // top-right round
                lineTo(r, b)                             // bottom-right sharp
                lineTo(l, b)                             // bottom-left sharp
                close()
            }
            drawPath(path, color)
        }


