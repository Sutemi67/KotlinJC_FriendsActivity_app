package apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.auth

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import apc.appcradle.kotlinjc_friendsactivity_app.MainViewModel
import apc.appcradle.kotlinjc_friendsactivity_app.ui.app_components.AppComponents

private enum class FieldState {
    Login, Password
}

@Composable
fun AuthScreen(
    viewModel: MainViewModel,
    onRegisterClick: () -> Unit
) {
    var fieldState by rememberSaveable { mutableStateOf(FieldState.Login) }
    var loginText by rememberSaveable { mutableStateOf("") }
    var passwordText by rememberSaveable { mutableStateOf("") }
    val transferState = viewModel.transferState.collectAsState().value

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
                Text(
                    modifier = Modifier.padding(15.dp),
                    text = "Hello, user!\nPlease login or register",
                    textAlign = TextAlign.Center
                )
                AnimatedVisibility(
                    visible = fieldState == FieldState.Login,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    AppComponents.AppInputField(
                        label = "enter your login",
                        value = loginText,
                        onValueChange = { loginText = it },
                        trailingIcon = if (loginText.isNotBlank()) Icons.Default.PlayArrow else null,
                        onIconClick = { fieldState = FieldState.Password }
                    )
                }
                AnimatedVisibility(
                    visible = fieldState == FieldState.Password,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    AppComponents.AppInputField(
                        label = "enter your password",
                        value = passwordText,
                        onValueChange = { passwordText = it },
                        trailingIcon = if (passwordText.isNotBlank()) Icons.Default.PlayArrow else null,
                        onIconClick = {
                            Log.d("dataTransfer", "$loginText, $passwordText")
                            viewModel.sendLoginData(loginText, passwordText)
                        },
                        needLeadingBackIcon = true,
                        onLeadingIconClick = { fieldState = FieldState.Login }
                    )
                }
                if (transferState.isLoading) {
                    LinearProgressIndicator(modifier = Modifier.padding(horizontal = 15.dp))
                }
                ElevatedButton(
                    modifier = Modifier.width(200.dp),
                    onClick = onRegisterClick
                ) { Text("or Register...") }
                if (transferState.errorMessage != null) {
                    Text(transferState.errorMessage)
                }
            }
        }
    }
}

//@Preview
//@Composable
//private fun Preview() {
//    KotlinJC_FriendsActivity_appTheme {
//        AuthScreen(sendLoginData = { log, pass -> {} }, onRegisterClick = {})
//    }
//}