package apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.auth.registration

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import apc.appcradle.kotlinjc_friendsactivity_app.MainViewModel
import apc.appcradle.kotlinjc_friendsactivity_app.ThemePreviewsNoUi
import apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.main.nav.toMainScreen
import apc.appcradle.kotlinjc_friendsactivity_app.ui.theme.KotlinJC_FriendsActivity_appTheme
import kotlinx.coroutines.launch

@Composable
fun RegistrationScreen(
    viewModel: MainViewModel,
    navController: NavController,
) {
    var loginText by remember { mutableStateOf("") }
    var passText by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val isSend: Boolean? = viewModel.isSend.collectAsState().value

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (isSend) {
                true -> {
                    Text("Удачно!!")
                }

                false -> {
                    Text("Не удачно прошло")
                }

                else -> {}
            }

            Text(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 15.dp),
                text = "Введите свой логин, по которому вы будете заходить в систему, а так же по которому вас смогут найти ваши друзья",
                textAlign = TextAlign.Center
            )
            OutlinedTextField(
                modifier = Modifier.padding(10.dp),
                value = loginText,
                onValueChange = { loginText = it },
                label = { Text("login") },
                shape = RoundedCornerShape(20.dp)
            )
            OutlinedTextField(
                modifier = Modifier.padding(10.dp),
                value = passText,
                onValueChange = { passText = it },
                label = { Text("password") },
                shape = RoundedCornerShape(20.dp)
            )
            ElevatedButton(
                modifier = Modifier.padding(10.dp),
                onClick = {
                    scope.launch {
                        if (viewModel.sendRegisterData(
                                loginText,
                                passText
                            ) == true
                        ) navController.toMainScreen()
                    }
                }
            ) { Text("Зарегистрироваться") }
        }
    }
}

@ThemePreviewsNoUi
@Composable
private fun Preview() {
    KotlinJC_FriendsActivity_appTheme {
//        RegistrationScreen()
    }
}