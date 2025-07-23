package apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.auth.registration

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import apc.appcradle.kotlinjc_friendsactivity_app.MainViewModel
import apc.appcradle.kotlinjc_friendsactivity_app.ThemePreviewsNoUi
import apc.appcradle.kotlinjc_friendsactivity_app.domain.model.DataTransferState
import apc.appcradle.kotlinjc_friendsactivity_app.ui.app_components.AppComponents
import apc.appcradle.kotlinjc_friendsactivity_app.ui.theme.KotlinJC_FriendsActivity_appTheme

@Composable
fun RegistrationScreen(
    viewModel: MainViewModel,
    toMainScreen: () -> Unit,
) {
    var loginText by remember { mutableStateOf("") }
    var passText by remember { mutableStateOf("") }
    val transferResult: DataTransferState by viewModel.transferState.collectAsState()

    LaunchedEffect(transferResult) {
        if (transferResult.isSuccessful == true && transferResult.errorMessage == null) {
            toMainScreen()
            Log.d("dataTransfer", "go to main through launched Effect")
        }
    }

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 15.dp),
                text = "Введите свой логин, по которому вы будете заходить в систему, а так же по которому вас смогут найти ваши друзья",
                textAlign = TextAlign.Center
            )
            AppComponents.AppInputField(
                label = "login",
                onValueChange = { loginText = it }
            )
            AppComponents.AppInputField(
                label = "password",
                onValueChange = { passText = it }
            )

            ElevatedButton(
                modifier = Modifier.padding(10.dp),
                onClick = { viewModel.sendRegisterData(loginText, passText) }
            ) { Text("Зарегистрироваться") }
            if (!transferResult.errorMessage.isNullOrEmpty()) {
                Text(transferResult.errorMessage!!)
            }
        }
    }
}

@ThemePreviewsNoUi
@Composable
private fun Preview() {
    KotlinJC_FriendsActivity_appTheme {
//        RegistrationScreen() {}
    }
}