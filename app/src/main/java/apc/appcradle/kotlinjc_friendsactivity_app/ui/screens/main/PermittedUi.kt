package apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import apc.appcradle.kotlinjc_friendsactivity_app.ui.theme.KotlinJC_FriendsActivity_appTheme

@Composable
fun PermittedUi(
    isStepSensorsAvailable: Boolean,
    stepCount: Int,
    isServiceRunning: Boolean,
    onTrueCallback: () -> Unit,
    onFalseCallback: () -> Unit,
) {
    if (!isStepSensorsAvailable) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Шагомер не поддерживается на этом устройстве",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
        return
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            style = MaterialTheme.typography.headlineLarge,
            text = "$stepCount"
        )
        Text(
            text = "Шагов за сегодня:",
            style = MaterialTheme.typography.bodyMedium,
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 15.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Column(
                modifier = Modifier.width(90.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("123456")
                Text("За неделю:")
            }
            Column(
                modifier = Modifier.width(90.dp),
                horizontalAlignment = Alignment.CenterHorizontally

            ) {
                Text("123")
                Text("Километров:")
            }
            Column(
                modifier = Modifier.width(90.dp),
                horizontalAlignment = Alignment.CenterHorizontally


            ) {
                Text("13")
                Text("Этаж:")
            }
        }
        Column(
            modifier = Modifier.padding(10.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Включить счетчик",
                    style = TextStyle(
                        fontSize = 17.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                )

                Switch(
                    checked = isServiceRunning,
                    onCheckedChange = { state ->
                        when (state) {
                            true -> {
                                onTrueCallback()
                            }

                            false -> {
                                onFalseCallback()
                            }
                        }
                    }
                )

            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    KotlinJC_FriendsActivity_appTheme {
        PermittedUi(
            true,
            stepCount = 2342,
            isServiceRunning = true,
            {},
            {})
    }
}