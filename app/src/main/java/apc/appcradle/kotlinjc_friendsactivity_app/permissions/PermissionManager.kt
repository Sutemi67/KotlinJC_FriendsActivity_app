package apc.appcradle.kotlinjc_friendsactivity_app.permissions

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PermissionManager(private val context: Context) {

    private val _permissionsGranted = MutableStateFlow(false)
    val permissionsGranted: StateFlow<Boolean> = _permissionsGranted.asStateFlow()

    private val requiredPermissions = mutableListOf(
        Manifest.permission.ACTIVITY_RECOGNITION
    ).apply {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            add(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    init {
        checkInitialPermissions()
    }

    private fun checkInitialPermissions() {
        val allGranted = requiredPermissions.all { permission ->
            ContextCompat.checkSelfPermission(
                context,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        }
        _permissionsGranted.value = allGranted
    }

    fun registerPermissionHandler(
        activity: ComponentActivity,
        onPermissionResult: (Boolean) -> Unit
    ) {
        val permissionLauncher = activity.registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            val allGranted = permissions.entries.all { it.value }
            _permissionsGranted.value = allGranted
            onPermissionResult(allGranted)

            if (!allGranted) {
                Toast.makeText(
                    context,
                    "Для работы шагомера требуются все разрешения",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        checkAndRequestPermissions(permissionLauncher)
    }

    fun checkAndRequestPermissions(
        permissionLauncher: ActivityResultLauncher<Array<String>>
    ) {
        val permissionsToRequest = requiredPermissions.filter { permission ->
            ContextCompat.checkSelfPermission(
                context,
                permission
            ) != PackageManager.PERMISSION_GRANTED
        }

        when {
            permissionsToRequest.isEmpty() -> {
                _permissionsGranted.value = true
            }
            else -> permissionLauncher.launch(permissionsToRequest.toTypedArray())
        }
    }

    fun arePermissionsGranted(): Boolean {
        return requiredPermissions.all { permission ->
            ContextCompat.checkSelfPermission(
                context,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        }
    }
} 