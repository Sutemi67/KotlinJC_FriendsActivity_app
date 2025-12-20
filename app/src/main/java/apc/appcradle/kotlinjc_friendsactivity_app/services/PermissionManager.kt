package apc.appcradle.kotlinjc_friendsactivity_app.services

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.core.content.ContextCompat
import apc.appcradle.kotlinjc_friendsactivity_app.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PermissionManager(private val context: Context) {

    private val _permissionsGranted = MutableStateFlow(false)
    val permissionsGranted: StateFlow<Boolean> = _permissionsGranted.asStateFlow()

    val requiredPermissions = mutableListOf<String>().apply {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            add(Manifest.permission.ACTIVITY_RECOGNITION)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            add(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    init {
        checkInitialPermissions()
    }

    private fun checkInitialPermissions() {
        _permissionsGranted.value = arePermissionsGranted()
    }

    fun onPermissionResult(isGranted: Boolean) {
        _permissionsGranted.value = arePermissionsGranted()
        if (!isGranted) {
            Toast.makeText(
                context,
                R.string.permissions_ask,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun arePermissionsGranted(): Boolean {
        val runtimeOk = requiredPermissions.all { permission ->
            ContextCompat.checkSelfPermission(
                context,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        }
        return runtimeOk
    }
}