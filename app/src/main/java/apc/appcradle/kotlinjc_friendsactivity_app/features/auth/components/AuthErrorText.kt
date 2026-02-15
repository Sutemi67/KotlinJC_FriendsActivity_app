package apc.appcradle.kotlinjc_friendsactivity_app.features.auth.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import apc.appcradle.kotlinjc_friendsactivity_app.core.app_theme.AppTextStyles
import apc.appcradle.kotlinjc_friendsactivity_app.core.app_theme.KotlinJC_FriendsActivity_appTheme
import apc.appcradle.kotlinjc_friendsactivity_app.network.model.DataTransferState
import apc.appcradle.kotlinjc_friendsactivity_app.features._common_components.AppText

@Composable
fun AuthErrorText(
    modifier: Modifier = Modifier,
    transferResult: DataTransferState
) {
    Box(modifier = modifier.height(50.dp), contentAlignment = Alignment.Center) {
        if (!transferResult.errorMessage.isNullOrEmpty()) {
            AppText(
                text = transferResult.errorMessage,
                color = Color.Red,
                appTextStyle = AppTextStyles.Body
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ErrorTextPreview() {
    KotlinJC_FriendsActivity_appTheme {
        AuthErrorText(transferResult = DataTransferState(errorMessage = "error 404"))
    }
}