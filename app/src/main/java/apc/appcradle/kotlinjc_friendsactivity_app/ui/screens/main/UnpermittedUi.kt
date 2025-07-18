package apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun UnpermittedUi(
    onGetPermissionsClick: () -> Unit
) {
    Column {
        Text(
            modifier = Modifier.padding(15.dp),
            text = "Для работы шагомера требуется разрешение на распознавание активности."
        )
        ElevatedButton(onClick = { onGetPermissionsClick() }) { Text("Получить разрешения") }
    }
}