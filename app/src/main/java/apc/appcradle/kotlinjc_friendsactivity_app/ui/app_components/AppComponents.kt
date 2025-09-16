package apc.appcradle.kotlinjc_friendsactivity_app.ui.app_components

import android.content.Intent
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import apc.appcradle.kotlinjc_friendsactivity_app.LocalAppTypography
import apc.appcradle.kotlinjc_friendsactivity_app.R
import apc.appcradle.kotlinjc_friendsactivity_app.domain.model.AppTextStyles
import apc.appcradle.kotlinjc_friendsactivity_app.ui.app_components.AppDialogs.AppDonationDialog
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
        screenRoute: String?,
    ) {
        val context = LocalContext.current
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
            AppDonationDialog(
                onDismiss = { isDialogVisible = false }
            )
        TopAppBar(
            title = {
                Crossfade(targetState = titleText) { text ->
                    AppText(text = text, appTextStyle = AppTextStyles.Header)
                }
            },
            actions = {
                IconButton(onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, "https://t.me/appcradle".toUri())
                    context.startActivity(intent)
                }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "send message",
                    )
                }
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
        text: String,
        modifier: Modifier = Modifier,
        color: Color = Color.Unspecified,
        singleLine: Boolean = false,
        textAlign: TextAlign = TextAlign.Start,
        appTextStyle: AppTextStyles = AppTextStyles.Body
    ) {
        Text(
            modifier = modifier,
            text = text,
            color = color,
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