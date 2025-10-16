package apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.settings

import android.widget.Toast
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import apc.appcradle.domain.models.AppThemes
import apc.appcradle.domain.models.local_data.SharedPreferencesData
import apc.appcradle.kotlinjc_friendsactivity_app.BuildConfig
import apc.appcradle.kotlinjc_friendsactivity_app.R
import apc.appcradle.kotlinjc_friendsactivity_app.ui.app_components.AppComponents.AppText
import apc.appcradle.kotlinjc_friendsactivity_app.ui.app_components.AppDialogs

@Composable
fun SettingsScreen(
    userLogin: String?,
    onLogoutClick: () -> Unit,
    onStepDistanceClick: (Double) -> Unit,
    onNicknameClick: (String, String) -> Unit = { s1, s2 -> },
    onScaleClick: (Float) -> Unit,
    onThemeClick: (AppThemes) -> Unit,
    settingsState: State<SharedPreferencesData>,
) {
    var isThemeDialogVisible by remember { mutableStateOf(false) }
    var isStepDialogVisible by remember { mutableStateOf(false) }
    var isScaleDialogVisible by remember { mutableStateOf(false) }
    var isLoginDialogVisible by remember { mutableStateOf(false) }

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
                AppText(
                    text = stringResource(R.string.auth_screen_login_placeholder),
                )
                Card(
                    modifier = Modifier.clickable { isLoginDialogVisible = true }
                ) {
                    AppText(
                        modifier = Modifier.padding(vertical = 10.dp, horizontal = 20.dp),
                        text = userLogin ?: "-",
                    )
                }
            }
            HorizontalDivider(Modifier.padding(horizontal = 15.dp))
            AppText(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                textAlign = TextAlign.Center,
                text = stringResource(R.string.settings_screen_login_hint)
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
                AppText(text = stringResource(R.string.settings_screen_your_step))
                Card(
                    modifier = Modifier.clickable { isStepDialogVisible = true }
                ) {
                    AppText(
                        modifier = Modifier.padding(vertical = 10.dp, horizontal = 20.dp),
                        text = stringResource(
                            R.string.settings_screen_your_step_in_meters,
                            settingsState.value.savedUserStep
                        ),
                        singleLine = true
                    )
                }
            }
            HorizontalDivider(Modifier.padding(horizontal = 15.dp))
            AppText(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                textAlign = TextAlign.Center,
                text = stringResource(R.string.settings_screen_step_length_hint)
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
                AppText(text = stringResource(R.string.settings_screen_type_scale))
                Card(
                    modifier = Modifier.clickable { isScaleDialogVisible = true }
                ) {
                    AppText(
                        modifier = Modifier.padding(vertical = 10.dp, horizontal = 20.dp),
                        text = when (settingsState.value.savedScale) {
                            0.5f -> "50%"
                            1.0f -> "100%"
                            1.5f -> "150%"
                            else -> "-"
                        }
                    )
                }
            }
            HorizontalDivider(Modifier.padding(horizontal = 15.dp))
            AppText(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                textAlign = TextAlign.Center,
                text = stringResource(R.string.settings_screen_type_scale_hint)
            )
        }
        ElevatedCard(
            modifier = Modifier
                .padding(vertical = 5.dp)
                .alpha(0.4f),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp, horizontal = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AppText(
                    text = stringResource(R.string.settings_screen_app_version),
                )
                Card {
                    AppText(
                        modifier = Modifier.padding(10.dp),
                        text = BuildConfig.VERSION_NAME,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
        Spacer(Modifier.height(20.dp))
        ElevatedButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            onClick = onLogoutClick
        ) {
            AppText(text = stringResource(R.string.settings_screen_logout))
        }
        ElevatedButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            onClick = { isThemeDialogVisible = true }
        ) {
            AppText(text = stringResource(R.string.settings_screen_change_theme))
        }
    }

    if (isThemeDialogVisible)
        AppDialogs.ThemeDialog(
            currentThemes = settingsState.value.savedTheme,
            onConfirmClick = { newTheme ->
                onThemeClick(newTheme)
                isThemeDialogVisible = false
            },
            onDismiss = { isThemeDialogVisible = false }
        )
    if (isStepDialogVisible)
        AppDialogs.NewStepValueDialog(
            onConfirmClick = { newStep -> onStepDistanceClick(newStep) },
            onDismiss = { isStepDialogVisible = false }
        )
    if (isScaleDialogVisible)
        AppDialogs.ScaleDialog(
            initialValue = settingsState.value.savedScale,
            onConfirm = { newValue ->
                onScaleClick(newValue)
            },
            onDismiss = { isScaleDialogVisible = false }
        )
    if (isLoginDialogVisible)
        when (userLogin) {
            null -> {
                Toast.makeText(
                    LocalContext.current,
                    stringResource(R.string.settings_screen_offline_logout_warning),
                    Toast.LENGTH_SHORT
                ).show()
            }

            else -> {
                AppDialogs.LoginChangeDialog(
                    onConfirmClick = { newLogin -> onNicknameClick(userLogin, newLogin) },
                    onDismiss = { isLoginDialogVisible = false }
                )
            }
        }
}