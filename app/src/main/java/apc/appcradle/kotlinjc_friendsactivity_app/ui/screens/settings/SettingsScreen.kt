package apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import apc.appcradle.kotlinjc_friendsactivity_app.ThemePreviews
import apc.appcradle.kotlinjc_friendsactivity_app.ui.theme.KotlinJC_FriendsActivity_appTheme

@Composable
fun SettingsScreen(
    userLogin: String = "Alex",
    onLogoutClick: () -> Unit,
    onStepDistanceClick: () -> Unit,
    onNicknameClick: () -> Unit = {},
    onThemeClick: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        ElevatedCard(
            modifier = Modifier.padding(vertical = 5.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp, horizontal = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Ваш логин:")
                Card(
                    modifier = Modifier.clickable { onNicknameClick() }
                ) {
                    Text(
                        modifier = Modifier.padding(vertical = 10.dp, horizontal = 20.dp),
                        text = userLogin
                    )
                }
            }
            HorizontalDivider(Modifier.padding(horizontal = 15.dp))
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                textAlign = TextAlign.Center,
                text = "Вы можете изменить свой логин"
            )
        }
        ElevatedCard(
            modifier = Modifier.padding(vertical = 5.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp, horizontal = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Длина вашего шага:")
                Card(
                    modifier = Modifier.clickable { onStepDistanceClick() }
                ) {
                    Text(
                        modifier = Modifier.padding(vertical = 10.dp, horizontal = 20.dp),
                        text = "0,3m"
                    )
                }
            }
            HorizontalDivider(Modifier.padding(horizontal = 15.dp))
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                textAlign = TextAlign.Center,
                text = "Если вы хотите указать более точное значение советую вам пройти известное расстояние (например, по карте) и разделить на число шагов"
            )
        }
        Spacer(Modifier.height(20.dp))
        ElevatedButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            onClick = onLogoutClick
        ) {
            Text("Logout")
        }
        ElevatedButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            onClick = onThemeClick
        ) {
            Text("Change theme")
        }
    }
}

@ThemePreviews
@Composable
private fun Preview() {
    KotlinJC_FriendsActivity_appTheme {
        SettingsScreen(onLogoutClick = {}, onStepDistanceClick = {})
    }
}