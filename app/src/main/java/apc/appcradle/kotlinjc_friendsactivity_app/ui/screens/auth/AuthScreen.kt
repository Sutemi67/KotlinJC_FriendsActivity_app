package apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.auth

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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import apc.appcradle.kotlinjc_friendsactivity_app.data.network.model.DataTransferState
import apc.appcradle.kotlinjc_friendsactivity_app.ui.app_components.AppBackgroundImage
import apc.appcradle.kotlinjc_friendsactivity_app.ui.app_components.AppComponents
import apc.appcradle.kotlinjc_friendsactivity_app.ui.app_components.AppComponents.AppText
import apc.appcradle.kotlinjc_friendsactivity_app.ui.theme.KotlinJC_FriendsActivity_appTheme

private enum class FieldState {
    Login, Password, Loading
}

@Composable
fun AuthScreen(
    sendLoginData: (String, String) -> Unit,
    transferState: DataTransferState,
    onRegisterClick: () -> Unit,
    onOfflineUseClick: () -> Unit
) {
    val passwordFocusRequester = remember { FocusRequester() }

    var fieldState by rememberSaveable { mutableStateOf(FieldState.Login) }
    var loginText by rememberSaveable { mutableStateOf("") }
    var passwordText by rememberSaveable { mutableStateOf("") }

    val isLoginValid by remember(loginText) {
        derivedStateOf { loginText.isNotBlank() }
    }

    LaunchedEffect(fieldState) {
        when (fieldState) {
            FieldState.Password -> passwordFocusRequester.requestFocus()
            else -> {}
        }
    }
    LaunchedEffect(transferState) {
        if (transferState.errorMessage != null && !transferState.isLoading) {
            fieldState = FieldState.Login
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
                    targetState = fieldState,
                    transitionSpec = {
                        fadeIn() + expandVertically() togetherWith fadeOut() + shrinkVertically()
                    },
                    label = "AuthFieldTransition"
                ) { state ->
                    when (state) {
                        FieldState.Login -> {
                            AppComponents.AppInputField(
                                modifier = Modifier
                                    .padding(vertical = 5.dp, horizontal = 35.dp)
                                    .sensitiveContent(),
                                label = stringResource(R.string.auth_screen_login_placeholder),
                                value = loginText,
                                onValueChange = { loginText = it },
                                trailingIcon = {
                                    if (isLoginValid) IconButton(onClick = {
                                        fieldState = FieldState.Password
                                    }) {
                                        Icon(Icons.AutoMirrored.Filled.ArrowForward, null)
                                    }
                                },
                            )
                        }

                        FieldState.Password -> {
                            AppComponents.AppInputField(
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
                                        fieldState = FieldState.Loading
                                        sendLoginData(loginText, passwordText)
                                    }) { Icon(Icons.AutoMirrored.Filled.ArrowForward, null) }
                                },
                                leadingIcon = {
                                    IconButton(onClick = {
                                        fieldState = FieldState.Login
                                    }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) }
                                },
                            )
                        }

                        FieldState.Loading -> {
                            AnimatedVisibility(
                                visible = transferState.isLoading
                            ) {
                                LinearProgressIndicator(modifier = Modifier.padding(horizontal = 15.dp))
                            }
                        }
                    }
                }
                ElevatedButton(
                    modifier = Modifier.width(260.dp), onClick = onRegisterClick
                ) { AppText(text = stringResource(R.string.auth_screen_create)) }
                ElevatedButton(
                    modifier = Modifier.width(260.dp), onClick = onOfflineUseClick
                ) { AppText(text = stringResource(R.string.auth_screen_offline)) }

                Box(Modifier.height(30.dp)) {
                    if (transferState.errorMessage != null) {
                        Text(
                            text = transferState.errorMessage,
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    KotlinJC_FriendsActivity_appTheme {
        AuthScreen(
            sendLoginData = { _, _ -> run {} },
            onRegisterClick = {},
            transferState = DataTransferState(),
            onOfflineUseClick = {}
        )
    }
}