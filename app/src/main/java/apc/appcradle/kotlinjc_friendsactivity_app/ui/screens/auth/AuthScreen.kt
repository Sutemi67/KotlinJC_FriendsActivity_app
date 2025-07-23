package apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import apc.appcradle.kotlinjc_friendsactivity_app.ui.app_components.AppComponents
import apc.appcradle.kotlinjc_friendsactivity_app.ui.theme.KotlinJC_FriendsActivity_appTheme

private enum class FieldState {
    Login, Password
}

@Composable
fun AuthScreen(
    sendLoginData: (String, String) -> Unit,
    onRegisterClick: () -> Unit
) {
    var fieldState by remember { mutableStateOf(FieldState.Login) }
    var loginText by remember { mutableStateOf("") }
    var passwordText by remember { mutableStateOf("") }

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
                when (fieldState) {
                    FieldState.Login -> {
                        AppComponents.AppInputField(
                            label = "enter your login",
                            onValueChange = { loginText = it },
                            trailingIcon = if (loginText.isNotBlank()) Icons.Default.PlayArrow else null,
                            onIconClick = { fieldState = FieldState.Password }
                        )
                    }

                    FieldState.Password -> {
                        AppComponents.AppInputField(
                            label = "enter your password",
                            onValueChange = { passwordText = it },
                            trailingIcon = if (passwordText.isNotBlank()) Icons.Default.PlayArrow else null,
                            onIconClick = { sendLoginData(loginText, passwordText) }
                        )
                    }
                }

                ElevatedButton(
                    modifier = Modifier.width(200.dp),
                    onClick = onRegisterClick
                ) { Text("or Register...") }
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    KotlinJC_FriendsActivity_appTheme {
        AuthScreen(sendLoginData = { log, pass -> {} }, onRegisterClick = {})
    }
}