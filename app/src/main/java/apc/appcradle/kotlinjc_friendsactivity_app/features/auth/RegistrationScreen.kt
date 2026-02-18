package apc.appcradle.kotlinjc_friendsactivity_app.features.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import apc.appcradle.kotlinjc_friendsactivity_app.R
import apc.appcradle.kotlinjc_friendsactivity_app.features._common_components.AppBackgroundImage
import apc.appcradle.kotlinjc_friendsactivity_app.features._common_components.AppInputField
import apc.appcradle.kotlinjc_friendsactivity_app.features._common_components.AppText
import apc.appcradle.kotlinjc_friendsactivity_app.features.auth.components.AuthButton
import apc.appcradle.kotlinjc_friendsactivity_app.features.auth.components.AuthErrorText
import apc.appcradle.kotlinjc_friendsactivity_app.features.auth.model.AuthEvents
import apc.appcradle.kotlinjc_friendsactivity_app.features.auth.model.AuthState
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun RegistrationScreen(
    toMainScreen: () -> Unit,
) {
    val viewModel: AuthViewModel = koinViewModel()
    val state = viewModel.state.collectAsState()
    RegistrationScreenUi(
        authState = state,
        toMainScreen = toMainScreen,
        sendRegisterCallback = { login, pass ->
            viewModel.obtainEvent(AuthEvents.Registration(login, pass))
        }
    )
}

@Composable
fun RegistrationScreenUi(
    authState: State<AuthState>,
    toMainScreen: () -> Unit,
    sendRegisterCallback: (String, String) -> Unit,
) {

    var loginText by rememberSaveable { mutableStateOf("") }
    var isLoginError by rememberSaveable { mutableStateOf(false) }
    var isPasswordError by rememberSaveable { mutableStateOf(false) }
    var passText by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(authState.value.dataTransferState) {
        if (authState.value.dataTransferState.isSuccessful == true && authState.value.dataTransferState.errorMessage == null) {
            toMainScreen()
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
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 15.dp),
                text = stringResource(R.string.register_screen_greeting),
                textAlign = TextAlign.Center
            )
            AppInputField(
                label = stringResource(R.string.auth_screen_login_placeholder),
                value = loginText,
                isError = isLoginError,
                onValueChange = {
                    loginText = it
                    isLoginError = false
                }
            )
            AppInputField(
                label = stringResource(R.string.auth_screen_password_placeholder),
                value = passText,
                isError = isPasswordError,
                onValueChange = {
                    passText = it
                    isPasswordError = false
                }
            )
            Box(Modifier.height(10.dp)) {
                if (authState.value.isLoading) {
                    LinearProgressIndicator(modifier = Modifier.padding(horizontal = 15.dp))
                }
            }
            AuthButton(textResource = R.string.auth_screen_create, onClick = {
                when {
                    loginText.isEmpty() -> {
                        isLoginError = true
                    }

                    passText.isEmpty() -> {
                        isPasswordError = true
                    }

                    else -> {
                        sendRegisterCallback(loginText, passText)
                    }
                }
            })
            AuthErrorText(transferResult = authState.value.dataTransferState)
        }
    }
}