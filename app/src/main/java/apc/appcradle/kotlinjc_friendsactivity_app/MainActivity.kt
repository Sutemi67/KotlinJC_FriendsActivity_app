package apc.appcradle.kotlinjc_friendsactivity_app

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import apc.appcradle.kotlinjc_friendsactivity_app.sensors.StepCounterService
import apc.appcradle.kotlinjc_friendsactivity_app.ui.screens.main.MainUserScreen
import apc.appcradle.kotlinjc_friendsactivity_app.ui.theme.KotlinJC_FriendsActivity_appTheme

class MainActivity : ComponentActivity() {
    private var requiredPermissionsGranted = false

    private val requiredPermissions = mutableListOf<String>().apply {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            add(Manifest.permission.POST_NOTIFICATIONS)
            add(Manifest.permission.ACTIVITY_RECOGNITION)
        }
    }

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        requiredPermissionsGranted = permissions.entries.all { it.value }

        if (!requiredPermissionsGranted) {
            Toast.makeText(
                this,
                "Для работы шагомера требуются все разрешения",
                Toast.LENGTH_SHORT
            ).show()
        }
        setUI()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        checkAndRequestPermissions()
        setUI()
    }

    private fun checkAndRequestPermissions() {
        val permissionsToRequest = requiredPermissions.filter { permission ->
            ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED
        }

        when {
            permissionsToRequest.isEmpty() -> {
                requiredPermissionsGranted = true
//                startStepCounterService()
            }

            else -> permissionLauncher.launch(permissionsToRequest.toTypedArray())
        }
    }

    private fun startStepCounterService() {
        val serviceIntent = Intent(this, StepCounterService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent)
        } else {
            startService(serviceIntent)
        }
    }

    private fun setUI() {
        setContent {
            KotlinJC_FriendsActivity_appTheme {
                Scaffold { paddingValues ->
                    MainUserScreen(
                        modifier = Modifier.padding(paddingValues),
                        isPermissionsGranted = requiredPermissionsGranted,
                        onStopClick = { stopService(Intent(this, StepCounterService::class.java)) },
                        onStartClick = { startStepCounterService() }
                    )
                }
            }
        }
    }
}