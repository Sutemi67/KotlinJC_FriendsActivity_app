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
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import apc.appcradle.kotlinjc_friendsactivity_app.sensors.AppSensorsManager
import apc.appcradle.kotlinjc_friendsactivity_app.sensors.StepCounterService
import apc.appcradle.kotlinjc_friendsactivity_app.ui.theme.KotlinJC_FriendsActivity_appTheme
import org.koin.compose.koinInject

class MainActivity : ComponentActivity() {
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private var isActivityRecognitionGranted = false
    private var isNotificationPermissionGranted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Запрос разрешения на уведомления для Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher =
                registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                    isNotificationPermissionGranted = isGranted
                    if (isGranted && isActivityRecognitionGranted) {
                        startStepCounterService()
                    }
                }

            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                isNotificationPermissionGranted = true
            }
        } else {
            isNotificationPermissionGranted = true
        }

        // Запрос разрешения на активность
        requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                isActivityRecognitionGranted = isGranted
                if (isGranted && isNotificationPermissionGranted) {
                    startStepCounterService()
                }
                if (!isGranted) {
                    Toast.makeText(
                        this,
                        "Для работы шагомера требуется разрешение на распознавание активности.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        checkAndRequestActivityRecognitionPermission()

        setContent {
            KotlinJC_FriendsActivity_appTheme {
                Scaffold { paddingValues ->
                    MainAppComposable(
                        Modifier.padding(paddingValues),
                        isActivityRecognitionGranted = isActivityRecognitionGranted,
                        onStopClick = { stopService(Intent(this, StepCounterService::class.java)) }
                    )
                }
            }
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

    private fun checkAndRequestActivityRecognitionPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACTIVITY_RECOGNITION
                ) == PackageManager.PERMISSION_GRANTED -> {
                    isActivityRecognitionGranted = true
                    if (isNotificationPermissionGranted) {
                        startStepCounterService()
                    }
                }

                else -> {
                    requestPermissionLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION)
                }
            }
        } else {
            isActivityRecognitionGranted = true
            if (isNotificationPermissionGranted) {
                startStepCounterService()
            }
        }
    }
}

@Composable
fun MainAppComposable(
    modifier: Modifier = Modifier,
    isActivityRecognitionGranted: Boolean = true,
    onStopClick: () -> Unit
) {
    val sensorManager = koinInject<AppSensorsManager>()
    val stepCount by sensorManager.stepsData.collectAsState()

    Box(modifier.fillMaxSize()) {
        if (!isActivityRecognitionGranted) {
            Text(
                modifier = Modifier.padding(15.dp),
                text = "Для работы шагомера требуется разрешение на распознавание активности."
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Шагов пройдено: $stepCount",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Button(
                    onClick = { sensorManager.resetSteps() },
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text("Сбросить счётчик")
                }

                Button(
                    onClick = {
                        sensorManager.stopCounting()
                        onStopClick()
                    }
                ) {
                    Text("Выйти из гонки")
                }
            }
        }
    }
}