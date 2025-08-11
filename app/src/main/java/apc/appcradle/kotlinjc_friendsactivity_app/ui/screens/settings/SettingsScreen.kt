package apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.settings

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import apc.appcradle.kotlinjc_friendsactivity_app.ThemePreviews
import apc.appcradle.kotlinjc_friendsactivity_app.domain.model.AppThemes
import apc.appcradle.kotlinjc_friendsactivity_app.ui.app_components.AppComponents
import apc.appcradle.kotlinjc_friendsactivity_app.ui.theme.ExpandedTypography
import apc.appcradle.kotlinjc_friendsactivity_app.ui.theme.KotlinJC_FriendsActivity_appTheme

@Composable
fun SettingsScreen(
    userLogin: String? = "Alex",
    userStepLength: Double,
    userScale: Float,
    onLogoutClick: () -> Unit,
    onStepDistanceClick: (Double) -> Unit,
    onNicknameClick: () -> Unit = {},
    onScaleClick: (Float) -> Unit,
    currentTheme: AppThemes,
    onThemeClick: (AppThemes) -> Unit,
    typo: Typography
) {
    var isThemeDialogVisible by remember { mutableStateOf(false) }
    var isStepDialogVisible by remember { mutableStateOf(false) }
    var isScaleDialogVisible by remember { mutableStateOf(false) }

    LaunchedEffect(currentTheme) {
        Log.i("theme", "settings screen theme first start painted to: $currentTheme")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState()),
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
                Text(
                    text = "Ваш логин:",
                    style = typo.bodyLarge
                )
                Card(
                    modifier = Modifier.clickable { onNicknameClick() }
                ) {
                    Text(
                        modifier = Modifier.padding(vertical = 10.dp, horizontal = 20.dp),
                        text = userLogin ?: "-"
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
                    modifier = Modifier.clickable { isStepDialogVisible = true }
                ) {
                    Text(
                        modifier = Modifier.padding(vertical = 10.dp, horizontal = 20.dp),
                        text = "$userStepLength m"
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
                Text("Масштаб шрифтов:")
                Card(
                    modifier = Modifier.clickable { isScaleDialogVisible = true }
                ) {
                    Text(
                        modifier = Modifier.padding(vertical = 10.dp, horizontal = 20.dp),
                        text = when (userScale) {
                            0.5f -> "50%"
                            1.0f -> "100%"
                            1.5f -> "150%"
                            else -> "-"
                        }
                    )
                }
            }
            HorizontalDivider(Modifier.padding(horizontal = 15.dp))
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                textAlign = TextAlign.Center,
                text = "Позволяет уменьшить или увеличить шрифты в приложении."
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
            onClick = { isThemeDialogVisible = true }
        ) {
            Text("Change theme")
        }
    }

    if (isThemeDialogVisible)
        AppComponents.ThemeDialog(
            currentThemes = currentTheme,
            onConfirmClick = { newTheme ->
                onThemeClick(newTheme)
                isThemeDialogVisible = false
            },
            onDismiss = { isThemeDialogVisible = false }
        )
    if (isStepDialogVisible)
        AppComponents.NewStepValueDialog(
            onConfirmClick = { newStep -> onStepDistanceClick(newStep) },
            onDismiss = { isStepDialogVisible = false }
        )
    if (isScaleDialogVisible)
        AppComponents.ScaleDialog(
            initialValue = userScale,
            onConfirm = { newValue ->
                onScaleClick(newValue)
                Log.i("scale", "new scale is $newValue")
            },
            onDismiss = { isScaleDialogVisible = false }
        )
}

@ThemePreviews
@Composable
private fun Preview() {
    KotlinJC_FriendsActivity_appTheme {
        SettingsScreen(
            onLogoutClick = {},
            userStepLength = 0.3,
            onStepDistanceClick = {},
            onThemeClick = {},
            currentTheme = AppThemes.Light,
            userScale = 1f,
            onScaleClick = {},
//            typo = CompactTypography
//            typo = MediumTypography
            typo = ExpandedTypography
        )
    }
}