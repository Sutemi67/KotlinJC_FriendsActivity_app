package apc.appcradle.kotlinjc_friendsactivity_app.ui.app_components

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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import apc.appcradle.kotlinjc_friendsactivity_app.LocalAppTypography
import apc.appcradle.kotlinjc_friendsactivity_app.R
import apc.appcradle.kotlinjc_friendsactivity_app.domain.model.AppTextStyles
import apc.appcradle.kotlinjc_friendsactivity_app.domain.model.AppThemes
import apc.appcradle.kotlinjc_friendsactivity_app.openDonate
import apc.appcradle.kotlinjc_friendsactivity_app.ui.app_components.AppComponents.AppText
import apc.appcradle.kotlinjc_friendsactivity_app.ui.app_components.AppComponents.ScaleSlider
import apc.appcradle.kotlinjc_friendsactivity_app.ui.app_components.AppDialogs.AppDonationDialog
import apc.appcradle.kotlinjc_friendsactivity_app.ui.theme.KotlinJC_FriendsActivity_appTheme

object AppDialogs {
    @Composable
    fun AppDonationDialog(
        onDismiss: () -> Unit,
    ) {
        val context = LocalContext.current
        AlertDialog(
            title = {
                AppText(
                    "Поддержать разработчика",
                    textAlign = TextAlign.Center,
                    appTextStyle = AppTextStyles.Header
                )
            },
            text = { AppText("Приложение полностью бесплатное, без рекламы и заточено под ваш комфорт.\n" +
                    "Если вдруг захотите отблагодарить разработчика — угостить кофе или помочь с развитием и поддержкой проекта — вы будете перенаправлены на страницу переводов.") },
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
                }) { AppText("Почему бы и нет") }
            },
            dismissButton = {
                ElevatedButton(onClick = onDismiss) { AppText("В другой раз") }
            },
        )
    }


    @Composable
    fun ScaleDialog(
        initialValue: (Float),
        onConfirm: (Float) -> Unit,
        onDismiss: () -> Unit,
    ) {
        var newValue by remember { mutableFloatStateOf(initialValue) }
        AlertDialog(
            title = {
                AppText(
                    text = "Изменить масштаб",
                    appTextStyle = AppTextStyles.Header
                )
            },
            text = {
                ScaleSlider(
                    initialValue = initialValue,
                    valueReturn = {
                        newValue = it
                    })
            },
            onDismissRequest = onDismiss,
            confirmButton = {
                ElevatedButton(onClick = {
                    onConfirm(newValue)
                    onDismiss()
                }) { AppText(text = "Применить") }
            },
            dismissButton = {
                ElevatedButton(onClick = onDismiss) { AppText(text = "Отмена") }
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
            title = { AppText(text = "Выбор темы", appTextStyle = AppTextStyles.Header) },
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
                                text = "Светлая"
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
                                text = "Темная"
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
                                text = "Системная"
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
                        text = "Обновить"
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
            title = { AppText(text = "Новое значение", appTextStyle = AppTextStyles.Header) },
            text = {
                OutlinedTextField(
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done,
                        keyboardType = KeyboardType.Decimal
                    ),
                    textStyle = LocalAppTypography.current.bodyText,
                    isError = isError,
                    shape = RoundedCornerShape(20.dp),
                    label = {
                        AppText(
                            text = "введите новое значение",
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
                        onConfirmClick(value.replace(",", ".").toDouble())
                        onDismiss()
                    } catch (e: Exception) {
                        isError = true
                        value = ""
                        Log.d("inputValue", "Ошибка ввода, ${e.message}")
                    }
                }) {
                    AppText(
                        text = "Confirm"
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