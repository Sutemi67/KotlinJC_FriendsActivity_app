package apc.appcradle.kotlinjc_friendsactivity_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import apc.appcradle.kotlinjc_friendsactivity_app.ui.theme.KotlinJC_FriendsActivity_appTheme
import org.koin.compose.koinInject

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KotlinJC_FriendsActivity_appTheme {
                Scaffold { paddingValues ->
                    MainAppComposable(Modifier.padding(paddingValues))
                }
            }
        }
    }
}

@Composable
fun MainAppComposable(
    modifier: Modifier = Modifier,
) {
    val sensorManager = koinInject<AppSensorsManager>()
    val sensorList = sensorManager.sensors

    Box(modifier.fillMaxSize()) {
        LazyColumn {
            items(sensorList.size) { index ->
                Text(
                    modifier = Modifier.padding(5.dp),
                    text = sensorList[index].toString()
                )
            }
        }
    }
}

//@ThemePreviews
//@Composable
//fun GreetingPreview() {
//    KotlinJC_FriendsActivity_appTheme {
//        MainAppComposable()
//    }
//}