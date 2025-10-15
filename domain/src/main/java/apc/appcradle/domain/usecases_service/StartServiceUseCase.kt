package apc.appcradle.domain.usecases_service

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat

class StartServiceUseCase(
    private val context: Context
) {
    operator fun invoke(serviceClass: Class<*>) {
        val serviceIntent = Intent(context, serviceClass)
        try {
            ContextCompat.startForegroundService(context, serviceIntent)
        } catch (e: Exception) {
            Log.e("service", "Failed to start service: ${e.message}")
        }
    }
}