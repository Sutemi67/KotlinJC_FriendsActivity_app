package apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.auth.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import apc.appcradle.kotlinjc_friendsactivity_app.ThemePreviews
import apc.appcradle.kotlinjc_friendsactivity_app.ui.theme.KotlinJC_FriendsActivity_appTheme

@Composable
fun LoginScreen(
    toMainScreen: () -> Unit,
    onRegisterClick: () -> Unit
) {
    Scaffold { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier.padding(15.dp),
                    text = "Hello, user!"
                )
                ElevatedButton(
                    modifier = Modifier.width(200.dp),
                    onClick = toMainScreen
                ) { Text("Login") }
                ElevatedButton(
                    modifier = Modifier.width(200.dp),
                    onClick = onRegisterClick
                ) { Text("Register") }
            }
        }
    }
}

@ThemePreviews
@Composable
private fun Preview() {
    KotlinJC_FriendsActivity_appTheme {
        LoginScreen(toMainScreen = {}, onRegisterClick = {})
    }
}