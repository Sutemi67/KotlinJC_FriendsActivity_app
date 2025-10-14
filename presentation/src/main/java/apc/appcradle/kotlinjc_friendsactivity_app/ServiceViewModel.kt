package apc.appcradle.kotlinjc_friendsactivity_app

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ServiceViewModel(
    context: Context,
) : ViewModel() {
    private val _isServiceWorkingState = MutableStateFlow(false)
    val isServiceWorkingState: StateFlow<Boolean> = _isServiceWorkingState.asStateFlow()

    init {
        updateServiceState(context)
    }

    fun updateServiceState(context: Context) {
        val isRun: Boolean = isServiceRunning(context, StepCounterService::class.java)
        _isServiceWorkingState.update { isRun }
    }

    fun startService(context: Context) {
        val serviceIntent = Intent(context, StepCounterService::class.java)
        try {
            ContextCompat.startForegroundService(context, serviceIntent)
            updateServiceState(context)
            _isServiceWorkingState.update { true }
        } catch (e: Exception) {
            Log.e("service", "Failed to start service: ${e.message}")
        }
    }

    fun stopService(context: Context) {
        val serviceIntent = Intent(context, StepCounterService::class.java)
        context.stopService(serviceIntent)
        updateServiceState(context)
        _isServiceWorkingState.update { false }
    }

    private fun isServiceRunning(context: Context, serviceClass: Class<*>): Boolean {
        val manager =
            context.getSystemService(Context.ACTIVITY_SERVICE) as android.app.ActivityManager
        return manager.getRunningServices(Integer.MAX_VALUE)
            .any { it.service.className == serviceClass.name }
    }

}