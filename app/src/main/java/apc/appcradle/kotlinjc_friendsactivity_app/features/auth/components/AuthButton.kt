package apc.appcradle.kotlinjc_friendsactivity_app.features.auth.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ElevatedButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import apc.appcradle.kotlinjc_friendsactivity_app.features._common_components.AppText

@Composable
fun AuthButton(
    modifier: Modifier = Modifier,
    @StringRes textResource: Int,
    onClick: () -> Unit
) {
    ElevatedButton(
        modifier = modifier.width(260.dp),
        onClick = onClick
    ) {
        AppText(text = stringResource(textResource))
    }
}