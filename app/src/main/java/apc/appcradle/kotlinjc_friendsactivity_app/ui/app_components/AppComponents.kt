package apc.appcradle.kotlinjc_friendsactivity_app.ui.app_components

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import apc.appcradle.kotlinjc_friendsactivity_app.R
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
            textStyle = TextStyle(textAlign = TextAlign.Center),
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
                        Text(
                            text = "Can't be empty",
                        )
                    }

                    false -> {
                        Text(
                            text = label,
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
                    Text(text = text)
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
                    onDismiss()
                }) { Text("Почему бы и нет") }
            },
            dismissButton = {
                ElevatedButton(onClick = onDismiss) { Text("В другой раз") }
            },
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