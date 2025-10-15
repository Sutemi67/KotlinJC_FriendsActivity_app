package apc.appcradle.domain.usecases_service

import android.content.Context

class CheckServiceStatusUseCase(
    private val context: Context
) {
    operator fun invoke(serviceClass: Class<*>): Boolean {
        val manager =
            context.getSystemService(Context.ACTIVITY_SERVICE) as android.app.ActivityManager
        return manager.getRunningServices(Integer.MAX_VALUE)
            .any { it.service.className == serviceClass.name }
    }
}