package apc.appcradle.kotlinjc_friendsactivity_app.features._common_components

import android.content.Intent
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import apc.appcradle.kotlinjc_friendsactivity_app.LocalAppTypography
import apc.appcradle.kotlinjc_friendsactivity_app.R
import apc.appcradle.kotlinjc_friendsactivity_app.core.app_theme.AppTextStyles
import apc.appcradle.kotlinjc_friendsactivity_app.core.app_theme.KotlinJC_FriendsActivity_appTheme
import apc.appcradle.kotlinjc_friendsactivity_app.core.utils.APP_ROUNDED_SHAPE
import apc.appcradle.kotlinjc_friendsactivity_app.core.utils.TELEGRAM_URL
import apc.appcradle.kotlinjc_friendsactivity_app.features.Destinations
import apc.appcradle.kotlinjc_friendsactivity_app.features._common_components.AppDialogs.AppDonationDialog
import apc.appcradle.kotlinjc_friendsactivity_app.features.auth.TokenRepository
import apc.appcradle.kotlinjc_friendsactivity_app.features.noAuthDestinations

@Composable
fun AppInputField(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    isPassword: Boolean = false,
    onValueChange: (String) -> Unit,
    trailingIcon: (@Composable () -> Unit)? = null,
    leadingIcon: (@Composable () -> Unit)? = null,
    isError: Boolean = false
) {
    val labelText =
        if (isError) {
            stringResource(R.string.components_inputs_labels_login_error)
        } else label
    val transformation =
        if (isPassword) PasswordVisualTransformation() else VisualTransformation.None
    OutlinedTextField(
        modifier = modifier.fillMaxWidth(),
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        isError = isError,
        visualTransformation = transformation,
        textStyle = LocalAppTypography.current.bodyText,
        trailingIcon = trailingIcon,
        leadingIcon = leadingIcon,
        label = {
            AppText(
                text = labelText,
                appTextStyle = AppTextStyles.Body
            )
        },
        shape = APP_ROUNDED_SHAPE
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    login: String,
    screenRoute: String?,
) {
    val iconsPadding = 6.dp
    val context = LocalContext.current

    val titleText = when (screenRoute) {
        Destinations.MAIN.route -> {
            if (login != TokenRepository.OFFLINE_USER_NICKNAME) stringResource(
                R.string.appbar_greeting_logged,
                login
            )
            else stringResource(R.string.appbar_greeting_offline)
        }

        Destinations.RATINGS.route -> stringResource(R.string.appbar_greeting_ratings)
        Destinations.SETTINGS.route -> stringResource(R.string.appbar_greeting_settings)
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
                AppText(text = text, appTextStyle = AppTextStyles.AppBarTitle)
            }
        },
        actions = {
            IconButton(onClick = {
                val intent = Intent(Intent.ACTION_VIEW, TELEGRAM_URL.toUri())
                context.startActivity(intent)
            }) {
                Box(
                    Modifier
                        .padding(iconsPadding)
                        .fillMaxHeight()
                        .aspectRatio(1f)
                ) {
                    Icon(
                        modifier = Modifier.fillMaxSize(),
                        painter = painterResource(R.drawable.telegram),
                        tint = Color(0xFF0088CC),
                        contentDescription = "send message",
                    )
                }
            }
            IconButton(onClick = { isDialogVisible = !isDialogVisible }) {
                Box(
                    Modifier
                        .padding(iconsPadding)
                        .fillMaxHeight()
                        .aspectRatio(1f)
                ) {
                    Icon(
                        modifier = Modifier.fillMaxSize(),
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "donate",
                        tint = Color.Red
                    )
                }
            }
        }
    )
}

@Composable
fun AppBottomNavBar(
    modifier: Modifier = Modifier,
    navDestinations: List<Destinations>,
    currentRoute: String?,
    onNavigate: (Destinations) -> Unit
) {
    if (currentRoute == Destinations.AUTH.route &&
        currentRoute == Destinations.REGISTER.route
    ) return

    NavigationBar(
        modifier = modifier,
        containerColor = Color.Transparent.copy(alpha = 0.05f)
    ) {

        navDestinations.forEach { item ->
            val isSelected = currentRoute == item.route
            NavigationBarItem(
                selected = isSelected,
                onClick = { onNavigate(item) },
                icon = {
                    Icon(
                        imageVector = if (isSelected) item.iconSelected else item.iconUnselected,
                        contentDescription = stringResource(item.label),
                    )
                },
                label = {
                    AppText(
                        stringResource(item.label),
                        appTextStyle = AppTextStyles.Body
                    )
                },
            )
        }
    }
}

@Preview
@Composable
fun AppBackgroundImage() {
    Image(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer {
                compositingStrategy = CompositingStrategy.Offscreen
            }
            .drawWithContent {
                drawContent()
                val gradient = Brush.verticalGradient(
                    colorStops = listOf(
                        0.1f to Color.Transparent,
                        0.5f to Color.Black,
                        0.9f to Color.Transparent
                    ).toTypedArray(),
                    startY = 0f,
                    endY = size.height
                )
                drawRect(
                    brush = gradient,
                    blendMode = BlendMode.DstIn
                )
            },
        painter = painterResource(R.drawable.ic_launcher_playstore),
        contentDescription = null,
        alpha = 0.2f,
        contentScale = ContentScale.Crop
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
    val typo = LocalAppTypography.current
    Text(
        modifier = modifier,
        text = text,
        color = color,
        overflow = TextOverflow.Ellipsis,
        maxLines = if (singleLine) 1 else Int.MAX_VALUE,
        textAlign = textAlign,
        style = when (appTextStyle) {
            AppTextStyles.Header -> typo.header
            AppTextStyles.Body -> typo.bodyText
            AppTextStyles.Label -> typo.labels
            AppTextStyles.MainCounter -> typo.mainStepCounter
            AppTextStyles.AppBarTitle -> typo.appBarTitle
        }
    )
}

@Preview
@Composable
private fun InputFieldPreview() {
    KotlinJC_FriendsActivity_appTheme {
        AppInputField(
            label = "login",
            onValueChange = {},
            value = "sdfg",
            leadingIcon = { IconButton(onClick = {}) { Icon(Icons.Default.PlayArrow, null) } },
            trailingIcon = {}
        )
    }
}

@Preview
@Composable
private fun AppTopBarPreview() {
    KotlinJC_FriendsActivity_appTheme {
        AppTopBar(screenRoute = Destinations.MAIN.route, login = "Alexander")
    }
}

@Preview
@Composable
private fun BottomBarPreview() {
    KotlinJC_FriendsActivity_appTheme {
        AppBottomNavBar(
            navDestinations = Destinations.noAuthDestinations,
            currentRoute = Destinations.SETTINGS.route
        ) { }
    }
}