package apc.appcradle.domain.usecases_service

import android.content.Context
import android.content.Intent

class StopServiceUseCase(
    private val context: Context
) {
    operator fun invoke(serviceClass: Class<*>) {
        val serviceIntent = Intent(context, serviceClass)
        context.stopService(serviceIntent)
    }
}