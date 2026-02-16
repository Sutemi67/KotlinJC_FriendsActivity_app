package apc.appcradle.kotlinjc_friendsactivity_app.features.settings

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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import apc.appcradle.kotlinjc_friendsactivity_app.BuildConfig
import apc.appcradle.kotlinjc_friendsactivity_app.R
import apc.appcradle.kotlinjc_friendsactivity_app.core.app_theme.AppThemes
import apc.appcradle.kotlinjc_friendsactivity_app.features._common_components.AppDialogs
import apc.appcradle.kotlinjc_friendsactivity_app.features._common_components.AppText
import apc.appcradle.kotlinjc_friendsactivity_app.features.settings.model.SettingsEvents
import apc.appcradle.kotlinjc_friendsactivity_app.features.settings.model.SettingsState
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SettingsScreen(
    vm: SettingsViewModel = koinViewModel()
) {
    val state = vm.state.collectAsStateWithLifecycle().value
    SettingsScreenUi(
        onLogoutClick = { vm.obtainEvent(SettingsEvents.Logout) },
        onStepDistanceClick = { vm.obtainEvent(SettingsEvents.ChangeStepLength(it)) },
        onLoginChange = { newLogin ->
            vm.obtainEvent(
                SettingsEvents.ChangeLogin(
                    newLogin = newLogin
                )
            )
        },
        onScaleClick = { vm.obtainEvent(SettingsEvents.ChangeScale(it)) },
        onThemeClick = { vm.obtainEvent(SettingsEvents.ChangeTheme(it)) },
        state = state
    )
}

@Composable
fun SettingsScreenUi(
    onLogoutClick: () -> Unit,
    onStepDistanceClick: (Double) -> Unit,
    onLoginChange: (String) -> Unit,
    onScaleClick: (Float) -> Unit,
    onThemeClick: (AppThemes) -> Unit,
    state: SettingsState,
) {
    val isThemeDialogVisible = remember { mutableStateOf(false) }
    val isStepDialogVisible = remember { mutableStateOf(false) }
    val isScaleDialogVisible = remember { mutableStateOf(false) }
    val isLoginDialogVisible = remember { mutableStateOf(false) }

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
                    modifier = Modifier.clickable { isLoginDialogVisible.value = true }
                ) {
                    AppText(
                        modifier = Modifier.padding(vertical = 10.dp, horizontal = 20.dp),
                        text = state.userLogin ?: "-",
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
                    modifier = Modifier.clickable { isStepDialogVisible.value = true }
                ) {
                    AppText(
                        modifier = Modifier.padding(vertical = 10.dp, horizontal = 20.dp),
                        text = stringResource(
                            R.string.settings_screen_your_step_in_meters,
                            state.userStepLength
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
                    modifier = Modifier.clickable { isScaleDialogVisible.value = true }
                ) {
                    AppText(
                        modifier = Modifier.padding(vertical = 10.dp, horizontal = 20.dp),
                        text = when (state.userScale) {
                            0.5f -> "50%"
                            1.0f -> "100%"
                            1.5f -> "150%"
                            else -> "-"
                        }
                    )
                }
            }
            HorizontalDivider(Modifier.padding(horizontal = 10.dp))
            AppText(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                textAlign = TextAlign.Center,
                text = stringResource(R.string.settings_screen_type_scale_hint)
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
            onClick = { isThemeDialogVisible.value = true }
        ) {
            AppText(text = stringResource(R.string.settings_screen_change_theme))
        }
        ElevatedButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 70.dp)
                .alpha(0.6f),
            onClick = onLogoutClick
        ) {
            AppText(text = stringResource(R.string.settings_screen_logout))
        }
    }

    if (isThemeDialogVisible.value)
        AppDialogs.ThemeDialog(
            currentThemes = state.currentTheme,
            onConfirmClick = { newTheme ->
                onThemeClick(newTheme)
                isThemeDialogVisible.value = false
            },
            onDismiss = { isThemeDialogVisible.value = false }
        )
    if (isStepDialogVisible.value)
        AppDialogs.NewStepValueDialog(
            onConfirmClick = { newStep -> onStepDistanceClick(newStep) },
            onDismiss = { isStepDialogVisible.value = false }
        )
    if (isScaleDialogVisible.value)
        AppDialogs.ScaleDialog(
            initialValue = state.userScale,
            onConfirm = { newValue ->
                onScaleClick(newValue)
            },
            onDismiss = { isScaleDialogVisible.value = false }
        )
    if (isLoginDialogVisible.value)
        when (state.userLogin) {
            null -> {
                Toast.makeText(
                    LocalContext.current,
                    stringResource(R.string.settings_screen_offline_logout_warning),
                    Toast.LENGTH_SHORT
                ).show()
            }

            else -> {
                AppDialogs.LoginChangeDialog(
                    onConfirmClick = { newLogin -> onLoginChange(newLogin) },
                    onDismiss = { isLoginDialogVisible.value = false }
                )
            }
        }
}

//@Preview
//@Composable
//private fun Preview() {
//
//    KotlinJC_FriendsActivity_appTheme {
//        CompositionLocalProvider(
//            LocalAppTypography provides CompactText
//        ) {
//            SettingsScreenUi(
//                onLogoutClick = {},
//                onStepDistanceClick = {},
//                onLoginChange = { _, _ -> },
//                onScaleClick = {},
//                onThemeClick = {},
//                state = AppState()
//            )
//        }
//    }
//}
//
//@Preview
//@Composable
//private fun Preview2() {
//
//    KotlinJC_FriendsActivity_appTheme {
//        CompositionLocalProvider(
//            LocalAppTypography provides MediumText
//        ) {
//            SettingsScreenUi(
//                onLogoutClick = {},
//                onStepDistanceClick = {},
//                onLoginChange = { _, _ -> },
//                onScaleClick = {},
//                onThemeClick = {},
//                state = AppState()
//
//            )
//        }
//    }
//}
//
//@Preview
//@Composable
//private fun Preview3() {
//
//    KotlinJC_FriendsActivity_appTheme {
//        CompositionLocalProvider(
//            LocalAppTypography provides ExpandedText
//        ) {
//            SettingsScreenUi(
//                onLogoutClick = {},
//                onStepDistanceClick = {},
//                onLoginChange = { _, _ -> },
//                onScaleClick = {},
//                onThemeClick = {},
//                state = AppState()
//            )
//        }
//    }
//}