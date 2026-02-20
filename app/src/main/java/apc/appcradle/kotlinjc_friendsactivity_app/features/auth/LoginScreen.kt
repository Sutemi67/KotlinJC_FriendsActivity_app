package apc.appcradle.kotlinjc_friendsactivity_app.features.auth

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.runtime.State
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import apc.appcradle.kotlinjc_friendsactivity_app.R
import apc.appcradle.kotlinjc_friendsactivity_app.features._common_components.AppBackgroundImage
import apc.appcradle.kotlinjc_friendsactivity_app.features._common_components.AppInputField
import apc.appcradle.kotlinjc_friendsactivity_app.features._common_components.AppText
import apc.appcradle.kotlinjc_friendsactivity_app.features.auth.components.AuthButton
import apc.appcradle.kotlinjc_friendsactivity_app.features.auth.components.AuthErrorText
import apc.appcradle.kotlinjc_friendsactivity_app.features.auth.components.AuthFieldStates
import apc.appcradle.kotlinjc_friendsactivity_app.features.auth.model.AuthEvents
import apc.appcradle.kotlinjc_friendsactivity_app.features.auth.model.AuthState
import org.koin.androidx.compose.koinViewModel

@Composable
fun LoginScreen(
    vm: AuthViewModel = koinViewModel(),
    navigateToRegister: () -> Unit,
) {
    val authState = vm.state.collectAsStateWithLifecycle()
    LoginScreenUi(
        sendLoginData = { login, pass -> vm.obtainEvent(AuthEvents.Login(login, pass)) },
        transferState = authState,
        navigateToRegister = navigateToRegister,
        onOfflineUseClick = { vm.obtainEvent(AuthEvents.GoOffline) }
    )
}

@Composable
fun LoginScreenUi(
    sendLoginData: (String, String) -> Unit,
    transferState: State<AuthState>,
    navigateToRegister: () -> Unit,
    onOfflineUseClick: () -> Unit
) {

    val passwordFocusRequester = remember { FocusRequester() }
    var authFieldState by rememberSaveable { mutableStateOf(AuthFieldStates.Login) }
    var loginText by rememberSaveable { mutableStateOf("") }
    var passwordText by rememberSaveable { mutableStateOf("") }

    val isLoginValid by remember(loginText) {
        derivedStateOf { loginText.isNotBlank() }
    }
    val isLoading = transferState.value.isLoading
    val error = transferState.value.dataTransferState.errorMessage

    // Если пошла загрузка — скрываем фокус. Если пришла ошибка — возвращаем на Login.
    LaunchedEffect(isLoading, error) {
        authFieldState = if (error == null && isLoading) {
            AuthFieldStates.Loading
        } else {
            AuthFieldStates.Login
        }
    }
    LaunchedEffect(authFieldState) {
        when (authFieldState) {
            AuthFieldStates.Password -> passwordFocusRequester.requestFocus()
            else -> {}
        }
    }

    Scaffold { paddingValues ->
        AppBackgroundImage()
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 15.dp)
                .fillMaxSize(),
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
                            visible = transferState.value.isLoading
                        ) {
                            LinearProgressIndicator(modifier = Modifier.padding(horizontal = 15.dp))
                        }
                    }
                }
            }
            AuthButton(textResource = R.string.auth_screen_create, onClick = navigateToRegister)
            AuthButton(textResource = R.string.auth_screen_offline, onClick = onOfflineUseClick)
            AuthErrorText(transferResult = transferState.value.dataTransferState)
        }
    }
}

//@Preview
//@Composable
//private fun PreviewAuth() {
//    KotlinJC_FriendsActivity_appTheme {
//        AuthScreenUi()
//    }
//}