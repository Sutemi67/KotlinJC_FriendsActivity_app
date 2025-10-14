package apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import apc.appcradle.kotlinjc_friendsactivity_app.R
import apc.appcradle.kotlinjc_friendsactivity_app.ui.theme.KotlinJC_FriendsActivity_appTheme

@Composable
fun UnpermittedUi(
    onGetPermissionsClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            modifier = Modifier.padding(15.dp),
            text = stringResource(R.string.permissions_asking)
        )
        ElevatedButton(onClick = { onGetPermissionsClick() }) { Text("Получить разрешения") }
    }
}

@Preview
@Composable
private fun Preview() {
    KotlinJC_FriendsActivity_appTheme {
        UnpermittedUi { }
    }
}