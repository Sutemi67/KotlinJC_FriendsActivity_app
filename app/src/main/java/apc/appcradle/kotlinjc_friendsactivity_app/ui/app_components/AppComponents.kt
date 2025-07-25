package apc.appcradle.kotlinjc_friendsactivity_app.ui.app_components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
        onLeadingIconClick: () -> Unit = {}
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
                Text(
                    text = label,
                )
            },
            shape = RoundedCornerShape(20.dp)
        )
    }


    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun AppTopBar(login: String?) {
        TopAppBar(
            title = {
                if (login != null)
                    Text("Have a good day, $login!")
                else
                    Text("Have a good day!")
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
        AppComponents.AppTopBar("AlexxMagnus")
    }
}