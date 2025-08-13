package apc.appcradle.kotlinjc_friendsactivity_app.ui.app_components

import android.util.Log
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import apc.appcradle.kotlinjc_friendsactivity_app.LocalAppTypography
import apc.appcradle.kotlinjc_friendsactivity_app.R
import apc.appcradle.kotlinjc_friendsactivity_app.domain.model.AppTextStyles
import apc.appcradle.kotlinjc_friendsactivity_app.domain.model.AppThemes
import apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.Destinations
import apc.appcradle.kotlinjc_friendsactivity_app.ui.theme.KotlinJC_FriendsActivity_appTheme

object AppComponents {

    @Composable
    fun AppInputField(
        modifier: Modifier = Modifier,
        label: String,
        value: String,
        onValueChange: (String) -> Unit,
        trailingIcon: ImageVector? = null,
        onIconClick: () -> Unit = {},
        needLeadingBackIcon: Boolean = false,
        onLeadingIconClick: () -> Unit = {},
        isError: Boolean = false
    ) {
        var inputText by rememberSaveable { mutableStateOf(value) }

        OutlinedTextField(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 50.dp, vertical = 5.dp),
            value = inputText,
            onValueChange = {
                inputText = it
                onValueChange(inputText)
            },
            singleLine = true,
            trailingIcon = {
                if (trailingIcon != null) {
                    IconButton(onClick = { onIconClick() }) {
                        Icon(
                            imageVector = trailingIcon,
                            contentDescription = null
                        )
                    }
                }
            },
            isError = isError,
            textStyle = LocalAppTypography.current.bodyText,
            leadingIcon = {
                if (needLeadingBackIcon)
                    IconButton({ onLeadingIconClick() }) {
                        Icon(
                            modifier = Modifier.graphicsLayer(scaleX = -1f),
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null
                        )
                    }
            },
            label = {
                when (isError) {
                    true -> {
                        AppText(
                            text = "Can't be empty",
                            appTextStyle = AppTextStyles.Body
                        )
                    }

                    false -> {
                        AppText(
                            text = label,
                            appTextStyle = AppTextStyles.Body
                        )
                    }
                }

            },
            shape = RoundedCornerShape(20.dp)
        )
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun AppTopBar(
        login: String?,
        screenRoute: String?
    ) {
        val titleText = when (screenRoute) {
            Destinations.MAIN.route -> {
                if (login != null) "Хорошего дня, $login!"
                else "Хорошего дня!"
            }

            Destinations.RATINGS.route -> "Недельная статистика"
            Destinations.SETTINGS.route -> "Настройки"
            else -> ""
        }
        var isDialogVisible by remember { mutableStateOf(false) }
        if (isDialogVisible)
            AppDonationDialog(onDismiss = { isDialogVisible = false })
        TopAppBar(
            title = {
                Crossfade(targetState = titleText) { text ->
                    AppText(text = text, appTextStyle = AppTextStyles.Header)
                }
            },
            actions = {
                IconButton(onClick = { isDialogVisible = !isDialogVisible }) {
                    Icon(
                        painter = painterResource(R.drawable.rounded_person_heart_24),
                        contentDescription = "donate",
                        tint = Color.Red
                    )
                }
            }
        )
    }

    @Composable
    fun AppDonationDialog(
        onDismiss: () -> Unit,
    ) {
        AlertDialog(
            title = { Text("Поддержать разработчика") },
            text = { Text("Приложение полностью бесплатное, без рекламы и рассчитано на удобное пользование. Если вы хотите поблагодарить разработчика и купить ему чашечку кофе, вы будете перенаправлены на страницу переводов.") },
            icon = {
                Icon(
                    painter = painterResource(R.drawable.outline_directions_run_24),
                    contentDescription = null
                )
            },
            onDismissRequest = onDismiss,
            confirmButton = {
                ElevatedButton(onClick = {
                    //todo дописать донаты
                    onDismiss()
                }) { Text("Почему бы и нет") }
            },
            dismissButton = {
                ElevatedButton(onClick = onDismiss) { Text("В другой раз") }
            },
        )
    }

    @Composable
    fun ScaleSlider(
        initialValue: Float,
        valueReturn: (Float) -> Unit
    ) {
        var sliderPosition by remember { mutableFloatStateOf(initialValue) }
        Slider(
            value = sliderPosition,
            onValueChange = {
                sliderPosition = it
                valueReturn(sliderPosition)
            },
            valueRange = 0.5f..1.5f,
            steps = 1
        )
    }

    @Composable
    fun AppText(
        modifier: Modifier = Modifier,
        text: String,
        singleLine: Boolean = false,
        textAlign: TextAlign = TextAlign.Start,
        appTextStyle: AppTextStyles = AppTextStyles.Body
    ) {
        Text(
            modifier = modifier,
            text = text,
            overflow = TextOverflow.Ellipsis,
            maxLines = if (singleLine) 1 else 200,
            textAlign = textAlign,
            style = when (appTextStyle) {
                AppTextStyles.Header -> {
                    LocalAppTypography.current.header
                }

                AppTextStyles.Body -> {
                    LocalAppTypography.current.bodyText
                }

                AppTextStyles.Label -> {
                    LocalAppTypography.current.labels
                }

                AppTextStyles.MainCounter -> {
                    LocalAppTypography.current.mainStepCounter
                }
            }
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
}

@Preview
@Composable
private fun ValueDialogPreview() {
    KotlinJC_FriendsActivity_appTheme {
        AppComponents.NewStepValueDialog(
            onConfirmClick = {},
            onDismiss = {}
        )
    }
}

@Preview
@Composable
private fun ThemeDialogPreview() {
    KotlinJC_FriendsActivity_appTheme {
        AppComponents.ThemeDialog(
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
        AppComponents.ScaleDialog(
            onConfirm = {},
            onDismiss = {},
            initialValue = 1.0f
        )
    }
}

@Preview
@Composable
private fun InputFieldPreview() {
    KotlinJC_FriendsActivity_appTheme {
        AppComponents.AppInputField(
            label = "login",
            onValueChange = {},
            value = "sdfg",
            needLeadingBackIcon = true
        )
    }
}

@Preview
@Composable
private fun AppTopBarPreview() {
    KotlinJC_FriendsActivity_appTheme {
        AppComponents.AppTopBar(screenRoute = Destinations.MAIN.route, login = "Alex")
    }
}

@Preview
@Composable
private fun DialogPreview() {
    KotlinJC_FriendsActivity_appTheme {
        AppComponents.AppDonationDialog(onDismiss = {})
    }
}