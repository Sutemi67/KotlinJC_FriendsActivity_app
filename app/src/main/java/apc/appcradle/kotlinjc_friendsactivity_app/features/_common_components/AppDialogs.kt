package apc.appcradle.kotlinjc_friendsactivity_app.features._common_components

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Slider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import apc.appcradle.kotlinjc_friendsactivity_app.LocalAppTypography
import apc.appcradle.kotlinjc_friendsactivity_app.R
import apc.appcradle.kotlinjc_friendsactivity_app.core.app_theme.AppTextStyles
import apc.appcradle.kotlinjc_friendsactivity_app.core.app_theme.AppThemes
import apc.appcradle.kotlinjc_friendsactivity_app.features._common_components.AppDialogs.AppDonationDialog
import apc.appcradle.kotlinjc_friendsactivity_app.core.app_theme.KotlinJC_FriendsActivity_appTheme
import apc.appcradle.kotlinjc_friendsactivity_app.core.utils.openDonate

object AppDialogs {
    @Composable
    fun AppDonationDialog(
        onDismiss: () -> Unit,
    ) {
        val context = LocalContext.current
        AlertDialog(
            title = {
                AppText(
                    stringResource(R.string.dialogs_titles_support),
                    textAlign = TextAlign.Center,
                    appTextStyle = AppTextStyles.Header
                )
            },
            text = { AppText(stringResource(R.string.dialogs_body_support)) },
            icon = {
                Icon(
                    painter = painterResource(R.drawable.outline_directions_run_24),
                    contentDescription = null
                )
            },
            onDismissRequest = onDismiss,
            confirmButton = {
                ElevatedButton(onClick = {
                    openDonate(context)
                    onDismiss()
                }) { AppText(stringResource(R.string.dialogs_buttons_support_yes)) }
            },
            dismissButton = {
                ElevatedButton(onClick = onDismiss) { AppText(stringResource(R.string.dialogs_buttons_support_no)) }
            },
        )
    }


    @Composable
    fun ScaleDialog(
        initialValue: Float,
        onConfirm: (Float) -> Unit,
        onDismiss: () -> Unit,
    ) {
        val newValue = remember { mutableFloatStateOf(initialValue) }
        AlertDialog(
            title = {
                AppText(
                    text = stringResource(R.string.dialogs_titles_scale),
                    appTextStyle = AppTextStyles.Header
                )
            },
            text = {
                Slider(
                    value = newValue.floatValue,
                    onValueChange = { newValue.floatValue = it },
                    valueRange = 0.5f..1.5f,
                    steps = 1
                )
            },
            onDismissRequest = onDismiss,
            confirmButton = {
                ElevatedButton(onClick = {
                    onConfirm(newValue.floatValue)
                    onDismiss()
                }) { AppText(text = stringResource(R.string.dialogs_buttons_scale_yes)) }
            },
            dismissButton = {
                ElevatedButton(onClick = onDismiss) { AppText(text = stringResource(R.string.dialogs_buttons_scale_no)) }
            },
        )
    }

    @Composable
    fun ThemeDialog(
        currentThemes: AppThemes,
        onConfirmClick: (AppThemes) -> Unit,
        onDismiss: () -> Unit
    ) {

        var selectedTheme by remember { mutableStateOf(currentThemes) }

        AlertDialog(
            title = {
                AppText(
                    text = stringResource(R.string.dialogs_titles_theme),
                    appTextStyle = AppTextStyles.Header
                )
            },
            text = {
                Column(Modifier.fillMaxWidth()) {
                    ElevatedCard(
                        modifier = Modifier
                            .padding(3.dp)
                            .clickable {
                                selectedTheme = AppThemes.Light
                            },
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AppText(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 15.dp),
                                text = stringResource(R.string.dialogs_buttons_theme_light)
                            )
                            RadioButton(
                                selected = selectedTheme == AppThemes.Light,
                                onClick = { selectedTheme = AppThemes.Light }
                            )
                        }
                    }
                    ElevatedCard(
                        modifier = Modifier
                            .padding(3.dp)
                            .clickable {
                                selectedTheme = AppThemes.Dark
                            },
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AppText(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 15.dp),
                                text = stringResource(R.string.dialogs_buttons_theme_dark)
                            )
                            RadioButton(
                                selected = selectedTheme == AppThemes.Dark,
                                onClick = { selectedTheme = AppThemes.Dark }
                            )
                        }
                    }
                    ElevatedCard(
                        modifier = Modifier
                            .padding(3.dp)
                            .clickable {
                                selectedTheme = AppThemes.System
                            },
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AppText(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 15.dp),
                                text = stringResource(R.string.dialogs_buttons_theme_system)
                            )
                            RadioButton(
                                selected = selectedTheme == AppThemes.System,
                                onClick = { selectedTheme = AppThemes.System }
                            )
                        }
                    }
                }
            },
            onDismissRequest = onDismiss,
            confirmButton = {
                ElevatedButton(onClick = { onConfirmClick(selectedTheme) }) {
                    AppText(
                        text = stringResource(R.string.dialogs_buttons_theme_yes)
                    )
                }
            },
        )
    }

    @Composable
    fun NewStepValueDialog(
        onConfirmClick: (Double) -> Unit,
        onDismiss: () -> Unit
    ) {
        var value by remember { mutableStateOf("") }
        var isError by remember { mutableStateOf(false) }

        AlertDialog(
            title = {
                AppText(
                    text = stringResource(R.string.dialogs_titles_step_length),
                    appTextStyle = AppTextStyles.Header
                )
            },
            text = {
                OutlinedTextField(
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done,
                        keyboardType = KeyboardType.Decimal
                    ),
                    textStyle = LocalAppTypography.current.bodyText,
                    isError = isError,
                    shape = RoundedCornerShape(20.dp),
                    value = value,
                    onValueChange = {
                        isError = false
                        value = it
                    }
                )
            },
            onDismissRequest = onDismiss,
            confirmButton = {
                ElevatedButton(onClick = {
                    try {
                        onConfirmClick(value.replace(",", ".").toDouble())
                        onDismiss()
                    } catch (_: Exception) {
                        isError = true
                        value = ""
                    }
                }) {
                    AppText(
                        text = stringResource(R.string.dialogs_buttons_scale_yes)
                    )
                }
            },
        )
    }

    @Composable
    fun LoginChangeDialog(
        onConfirmClick: (String) -> Unit,
        onDismiss: () -> Unit
    ) {
        var value by remember { mutableStateOf("") }
        var isError by remember { mutableStateOf(false) }

        AlertDialog(
            title = { AppText(text = "Введите новый логин", appTextStyle = AppTextStyles.Header) },
            text = {
                OutlinedTextField(
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done,
                        keyboardType = KeyboardType.Text
                    ),
                    textStyle = LocalAppTypography.current.bodyText,
                    isError = isError,
                    shape = RoundedCornerShape(20.dp),
                    label = {
                        AppText(
                            text = "новый ник",
                            appTextStyle = AppTextStyles.Body
                        )
                    },
                    value = value,
                    onValueChange = {
                        isError = false
                        value = it
                    }
                )
            },
            onDismissRequest = onDismiss,
            confirmButton = {
                ElevatedButton(onClick = {
                    try {
                        onConfirmClick(value)
                        onDismiss()
                    } catch (e: Exception) {
                        isError = true
                        value = ""
                        Log.d("inputValue", "Ошибка ввода, ${e.message}")
                    }
                }) {
                    AppText(
                        text = "Принять"
                    )
                }
            },
            dismissButton = {
                ElevatedButton(onClick = {
                    onDismiss()
                }) {
                    AppText(
                        text = "Отмена"
                    )
                }
            }
        )
    }
}

@Preview
@Composable
private fun DialogPreview() {
    KotlinJC_FriendsActivity_appTheme {
        AppDonationDialog(onDismiss = {})
    }
}

@Preview
@Composable
private fun ThemeDialogPreview() {
    KotlinJC_FriendsActivity_appTheme {
        AppDialogs.ThemeDialog(
            currentThemes = AppThemes.Dark,
            onConfirmClick = {},
            onDismiss = {}
        )

    }
}

@Preview
@Composable
private fun ScaleDialogPreview() {
    KotlinJC_FriendsActivity_appTheme {
        AppDialogs.ScaleDialog(
            onConfirm = {},
            onDismiss = {},
            initialValue = 1.0f
        )
    }
}

@Preview
@Composable
private fun ValueDialogPreview() {
    KotlinJC_FriendsActivity_appTheme {
        AppDialogs.NewStepValueDialog(
            onConfirmClick = {},
            onDismiss = {}
        )
    }
}