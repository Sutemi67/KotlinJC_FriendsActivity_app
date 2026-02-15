package apc.appcradle.kotlinjc_friendsactivity_app.features.auth

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.sensitiveContent
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import apc.appcradle.kotlinjc_friendsactivity_app.R
import apc.appcradle.kotlinjc_friendsactivity_app.core.app_theme.KotlinJC_FriendsActivity_appTheme
import apc.appcradle.kotlinjc_friendsactivity_app.network.model.DataTransferState
import apc.appcradle.kotlinjc_friendsactivity_app.features._common_components.AppBackgroundImage
import apc.appcradle.kotlinjc_friendsactivity_app.features._common_components.AppInputField
import apc.appcradle.kotlinjc_friendsactivity_app.features._common_components.AppText
import apc.appcradle.kotlinjc_friendsactivity_app.features.auth.components.AuthButton
import apc.appcradle.kotlinjc_friendsactivity_app.features.auth.components.AuthErrorText
import apc.appcradle.kotlinjc_friendsactivity_app.features.auth.components.AuthFieldStates

@Composable
fun AuthScreen(
    sendLoginData: (String, String) -> Unit,
    transferState: DataTransferState,
    onRegisterClick: () -> Unit,
    onOfflineUseClick: () -> Unit
) {
    val passwordFocusRequester = remember { FocusRequester() }

    var authFieldState by rememberSaveable { mutableStateOf(AuthFieldStates.Login) }
    var loginText by rememberSaveable { mutableStateOf("") }
    var passwordText by rememberSaveable { mutableStateOf("") }

    val isLoginValid by remember(loginText) {
        derivedStateOf { loginText.isNotBlank() }
    }

    LaunchedEffect(authFieldState) {
        when (authFieldState) {
            AuthFieldStates.Password -> passwordFocusRequester.requestFocus()
            else -> {}
        }
    }
    LaunchedEffect(transferState) {
        if (transferState.errorMessage != null && !transferState.isLoading) {
            authFieldState = AuthFieldStates.Login
        }
    }

    Scaffold { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center,
        ) {
            AppBackgroundImage()
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AppText(
                    modifier = Modifier.padding(15.dp),
                    text = stringResource(R.string.auth_screen_greeting),
                    textAlign = TextAlign.Center,
                )
                AnimatedContent(
                    modifier = Modifier.padding(vertical = 15.dp),
                    targetState = authFieldState,
                    transitionSpec = {
                        fadeIn() + expandVertically() togetherWith fadeOut() + shrinkVertically()
                    },
                    label = "AuthFieldTransition"
                ) { state ->
                    when (state) {
                        AuthFieldStates.Login -> {
                            AppInputField(
                                modifier = Modifier
                                    .padding(vertical = 5.dp, horizontal = 35.dp)
                                    .sensitiveContent(),
                                label = stringResource(R.string.auth_screen_login_placeholder),
                                value = loginText,
                                onValueChange = { loginText = it },
                                trailingIcon = {
                                    if (isLoginValid) IconButton(onClick = {
                                        authFieldState = AuthFieldStates.Password
                                    }) {
                                        Icon(Icons.AutoMirrored.Filled.ArrowForward, null)
                                    }
                                },
                            )
                        }

                        AuthFieldStates.Password -> {
                            AppInputField(
                                modifier = Modifier
                                    .padding(vertical = 5.dp, horizontal = 35.dp)
                                    .focusRequester(passwordFocusRequester)
                                    .sensitiveContent(),
                                label = stringResource(R.string.auth_screen_password_placeholder),
                                value = passwordText,
                                isPassword = true,
                                onValueChange = { passwordText = it },
                                trailingIcon = {
                                    if (loginText.isNotBlank()) IconButton(onClick = {
                                        authFieldState = AuthFieldStates.Loading
                                        sendLoginData(loginText, passwordText)
                                    }) { Icon(Icons.AutoMirrored.Filled.ArrowForward, null) }
                                },
                                leadingIcon = {
                                    IconButton(onClick = {
                                        authFieldState = AuthFieldStates.Login
                                    }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) }
                                },
                            )
                        }

                        AuthFieldStates.Loading -> {
                            AnimatedVisibility(
                                visible = transferState.isLoading
                            ) {
                                LinearProgressIndicator(modifier = Modifier.padding(horizontal = 15.dp))
                            }
                        }
                    }
                }
                AuthButton(textResource = R.string.auth_screen_create, onClick = onRegisterClick)
                AuthButton(textResource = R.string.auth_screen_offline, onClick = onOfflineUseClick)
                AuthErrorText(transferResult = transferState)
            }
        }
    }
}

@Preview
@Composable
private fun PreviewAuth() {
    KotlinJC_FriendsActivity_appTheme {
        AuthScreen(
            sendLoginData = { _, _ -> },
            onRegisterClick = {},
            transferState = DataTransferState(errorMessage = "error 404"),
            onOfflineUseClick = {}
        )
    }
}