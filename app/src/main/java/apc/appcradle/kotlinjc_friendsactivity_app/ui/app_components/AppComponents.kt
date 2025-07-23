package apc.appcradle.kotlinjc_friendsactivity_app.ui.app_components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

object AppComponents {

    @Composable
    fun AppInputField(
        modifier: Modifier = Modifier,
        label: String,
        onValueChange: (String) -> Unit,
        trailingIcon: ImageVector? = null,
        onIconClick: () -> Unit? = { null }
    ) {

        var inputText by remember { mutableStateOf("") }

        OutlinedTextField(
            modifier = modifier.padding(10.dp),
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
            label = { Text(label) },
            shape = RoundedCornerShape(20.dp)
        )
    }
}