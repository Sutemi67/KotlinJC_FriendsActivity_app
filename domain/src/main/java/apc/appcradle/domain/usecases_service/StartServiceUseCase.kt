package apc.appcradle.domain.usecases_service

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat

class StartServiceUseCase(
    private val context: Context,
) {
    operator fun invoke(serviceClass: Class<*>) {
        try {
            val serviceIntent = Intent(context, serviceClass)
            ContextCompat.startForegroundService(context, serviceIntent)
        } catch (e: Exception) {
            Log.e("service", "Failed to start service: ${e.message}")
        }
    }
}