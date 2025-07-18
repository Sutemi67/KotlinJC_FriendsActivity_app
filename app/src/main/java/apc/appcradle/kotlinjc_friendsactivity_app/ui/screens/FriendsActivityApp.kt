package apc.appcradle.kotlinjc_friendsactivity_app.ui.screens

import android.Manifest
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.LocalActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import apc.appcradle.kotlinjc_friendsactivity_app.MainViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun FriendsActivityApp(
    viewModel: MainViewModel = koinViewModel(),
    startService: () -> Unit,
    stopService: () -> Unit
) {

}