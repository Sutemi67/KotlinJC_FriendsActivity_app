package apc.appcradle.kotlinjc_friendsactivity_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import apc.appcradle.kotlinjc_friendsactivity_app.ui.NavigationHost
import apc.appcradle.kotlinjc_friendsactivity_app.ui.theme.KotlinJC_FriendsActivity_appTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KotlinJC_FriendsActivity_appTheme {
                NavigationHost()
            }
        }
    }
}