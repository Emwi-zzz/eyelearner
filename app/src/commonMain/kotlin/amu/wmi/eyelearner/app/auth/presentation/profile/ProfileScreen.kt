package amu.wmi.eyelearner.app.auth.presentation.profile

import amu.wmi.eyelearner.app.navigation.LabeledInputField
import amu.wmi.eyelearner.app.navigation.MenuType
import amu.wmi.eyelearner.app.navigation.NavBar
import amu.wmi.eyelearner.app.navigation.NavigationData
import amu.wmi.eyelearner.app.theme.Background
import amu.wmi.eyelearner.app.theme.LocalThemeManager
import amu.wmi.eyelearner.app.theme.MenuCreature
import amu.wmi.eyelearner.app.theme.ThemeManager
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ProfileRoute(
    navData: NavigationData,
    onLoggedOut: () -> Unit = {},
    onNavigateToLogin: () -> Unit = onLoggedOut,
    viewModel: ProfileViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state.isLoggedOut) {
        if (state.isLoggedOut) onNavigateToLogin()
    }

    ProfileScreen(
        navData = navData,
        state = state,
        onIntent = viewModel::handleIntent,
        onNavigateToLogin = onNavigateToLogin
    )
}

@Composable
fun ProfileScreen(
    navData: NavigationData,
    state: ProfileScreenState,
    onIntent: (ProfileIntent) -> Unit,
    onNavigateToLogin: () -> Unit = {}
) {
    val theme: ThemeManager = LocalThemeManager.current
    val pickAvatar = rememberAvatarPicker { uri ->
        onIntent(ProfileIntent.UpdateAvatar(uri))
    }

    val snackbarHostState = remember { SnackbarHostState() }

    // Surface messages via a snackbar
    LaunchedEffect(state.savedMessage, state.errorMessage) {
        val msg = state.errorMessage ?: state.savedMessage
        if (msg != null) {
            snackbarHostState.showSnackbar(msg)
            onIntent(ProfileIntent.DismissMessage)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Background(
            backgroundColor = theme.creatureTheme.background_color,
            squareColors = theme.creatureTheme.backgroundBody_color,
            modifier = Modifier
                .fillMaxSize()
                .background(color = theme.creatureTheme.background_color)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                NavBar(
                    onIntent = onIntent,
                    theme = theme,
                    navData = navData,
                    openedMenu = MenuType.profile,
                    openedMenuColor = theme.appTheme.text_color,
                    otherMenuColor = theme.appTheme.tertiaryText_color,
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        modifier = Modifier
                            .widthIn(min = 800.dp, max = 1400.dp)
                            .fillMaxWidth()
                            .heightIn(min = 600.dp, max = 800.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = theme.appTheme.mainPanel_color
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 32.dp, vertical = 32.dp),
                            horizontalAlignment = Alignment.Start,
                            verticalArrangement = Arrangement.Top
                        ) {
                            // ---------- Avatar ----------
                            Box(
                                modifier = Modifier
                                    .padding(start = 30.dp, top = 10.dp)
                                    .size(140.dp)
                                    .clip(CircleShape)
                                    .background(theme.appTheme.tertiaryText_color.copy(alpha = 0.2f))
                                    .then(
                                        if (state.isEditing)
                                            Modifier.clickable { pickAvatar() }
                                        else Modifier
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                val avatarModel = state.editedAvatarUri
                                if (avatarModel != null) {
                                    AsyncImage(
                                        model = avatarModel,
                                        contentDescription = "User avatar",
                                        modifier = Modifier.fillMaxSize().clip(CircleShape),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Text(
                                        text = (state.editedUsername.firstOrNull() ?: 'U')
                                            .uppercaseChar().toString(),
                                        fontSize = 60.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = theme.appTheme.text_color
                                    )
                                }
                                if (state.isEditing) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(CircleShape)
                                            .background(Color.Black.copy(alpha = 0.35f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("Change", color = Color.White, fontSize = 16.sp)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(30.dp))

                            // ---------- Username ----------
                            if (state.isEditing) {
                                LabeledInputField(
                                    theme = theme,
                                    label = "Username",
                                    value = state.editedUsername,
                                    onValueChange = { onIntent(ProfileIntent.UpdateUsername(it)) },
                                    fontSize = 40.sp,
                                    modifier = Modifier
                                        .padding(start = 30.dp)
                                        .width(400.dp)
                                )
                            } else {
                                Text(
                                    text = state.editedUsername.ifBlank { "User" },
                                    fontSize = 70.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = theme.appTheme.text_color,
                                    modifier = Modifier.padding(start = 30.dp)
                                )
                            }

                            if (state.errorMessage != null) {
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    text = state.errorMessage,
                                    color = MaterialTheme.colorScheme.error,
                                    fontSize = 16.sp,
                                    modifier = Modifier.padding(start = 30.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(30.dp))

                            // ---------- Actions ----------
                            Row(
                                modifier = Modifier.padding(start = 30.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                if (state.isEditing) {
                                    Button(
                                        onClick = { onIntent(ProfileIntent.SaveProfile) },
                                        enabled = !state.isSaving,
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = theme.appTheme.text_color
                                        )
                                    ) {
                                        if (state.isSaving) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(18.dp),
                                                strokeWidth = 2.dp,
                                                color = theme.appTheme.mainPanel_color
                                            )
                                            Spacer(Modifier.width(8.dp))
                                        }
                                        Text("Save", fontSize = 20.sp)
                                    }
                                    Button(
                                        onClick = { onIntent(ProfileIntent.CancelEditing) },
                                        enabled = !state.isSaving,
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = theme.appTheme.tertiaryText_color
                                        )
                                    ) {
                                        Text("Cancel", fontSize = 20.sp)
                                    }
                                } else {
                                    Button(
                                        onClick = { onIntent(ProfileIntent.StartEditing) },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = theme.appTheme.text_color
                                        )
                                    ) {
                                        Text("Edit profile", fontSize = 20.sp)
                                    }
                                    // NEW: logout entry point
                                    Button(
                                        onClick = { onIntent(ProfileIntent.Logout) },
                                        enabled = !state.isLoading,
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = theme.appTheme.tertiaryText_color
                                        )
                                    ) {
                                        if (state.isLoading) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(18.dp),
                                                strokeWidth = 2.dp,
                                                color = theme.appTheme.mainPanel_color
                                            )
                                            Spacer(Modifier.width(8.dp))
                                        }
                                        Text("Log out", fontSize = 20.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        MenuCreature(
            theme = theme,
            heightMin = 0.290f,
            heightMax = 0.3f,
            modifier = Modifier.align(
                BiasAlignment(horizontalBias = 4f / 6f, verticalBias = 1f)
            )
        )

        // Snackbar host so messages actually show up
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp)
        )
    }
}

@Composable
expect fun rememberAvatarPicker(onPicked: (String) -> Unit): () -> Unit


