package apc.appcradle.domain.usecases_service

import android.content.Context
import android.content.Intent
import android.util.Log

class StopServiceUseCase(
    private val context: Context,
) {
    operator fun invoke(serviceClass: Class<*>) {
        try {
            val serviceIntent = Intent(context, serviceClass)
            context.stopService(serviceIntent)
        } catch (e: Exception) {
            Log.e("service", "${e.message}")
        }
    }
}