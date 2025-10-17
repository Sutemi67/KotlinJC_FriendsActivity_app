package apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import apc.appcradle.kotlinjc_friendsactivity_app.NetworkAppState
import apc.appcradle.kotlinjc_friendsactivity_app.R
import apc.appcradle.kotlinjc_friendsactivity_app.ui.app_components.AppComponents
import apc.appcradle.kotlinjc_friendsactivity_app.ui.app_components.AppComponents.AppText

private enum class FieldState {
    Login, Password
}

@Composable
fun AuthScreen(
    sendLoginData: (String, String) -> Unit,
    networkState: State<NetworkAppState>,
    onRegisterClick: () -> Unit,
    onOfflineUseClick: () -> Unit
) {
    var fieldState by rememberSaveable { mutableStateOf(FieldState.Login) }
    var loginText by rememberSaveable { mutableStateOf("") }
    var passwordText by rememberSaveable { mutableStateOf("") }

    Scaffold { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AppText(
                    modifier = Modifier.padding(15.dp),
                    text = stringResource(R.string.auth_screen_greeting),
                    textAlign = TextAlign.Center,
                )
                AnimatedVisibility(
                    visible = fieldState == FieldState.Login,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    AppComponents.AppInputField(
                        label = stringResource(R.string.auth_screen_login_placeholder),
                        value = loginText,
                        onValueChange = { loginText = it },
                        trailingIcon = if (loginText.isNotBlank()) Icons.Default.PlayArrow else null,
                        onIconClick = { fieldState = FieldState.Password })
                }
                AnimatedVisibility(
                    visible = fieldState == FieldState.Password,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    AppComponents.AppInputField(
                        label = stringResource(R.string.auth_screen_password_placeholder),
                        value = passwordText,
                        onValueChange = { passwordText = it },
                        trailingIcon = if (passwordText.isNotBlank()) Icons.Default.PlayArrow else null,
                        onIconClick = { sendLoginData(loginText, passwordText) },
                        needLeadingBackIcon = true,
                        onLeadingIconClick = { fieldState = FieldState.Login })
                }
                Box(Modifier.height(10.dp)) {
                    if (networkState.value.isLoading) {
                        LinearProgressIndicator(modifier = Modifier.padding(horizontal = 15.dp))
                    }
                }
                ElevatedButton(
                    modifier = Modifier.width(260.dp), onClick = onRegisterClick
                ) { AppText(text = stringResource(R.string.auth_screen_create)) }
                ElevatedButton(
                    modifier = Modifier.width(260.dp), onClick = onOfflineUseClick
                ) { AppText(text = stringResource(R.string.auth_screen_offline)) }

                Box(Modifier.height(30.dp)) {
                    if (networkState.value.errorMessage != null) Text(text = networkState.value.errorMessage!!)
                }
            }
        }
    }
}