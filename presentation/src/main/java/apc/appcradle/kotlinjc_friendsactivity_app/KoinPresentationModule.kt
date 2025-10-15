package apc.appcradle.kotlinjc_friendsactivity_app

import android.content.Context
import android.content.SharedPreferences
import android.hardware.SensorEventListener
import android.util.Log
import androidx.work.WorkManager
import apc.appcradle.domain.SettingsRepository
import apc.appcradle.domain.TokenRepository
import apc.appcradle.kotlinjc_friendsactivity_app.presentation.view_models.NetworkViewModel
import apc.appcradle.kotlinjc_friendsactivity_app.presentation.view_models.ServiceViewModel
import apc.appcradle.kotlinjc_friendsactivity_app.presentation.view_models.SettingsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val koinPresentationModule = module {
    singleOf(::PermissionManager)

    viewModelOf(::NetworkViewModel)
    viewModelOf(::SettingsViewModel)
    viewModelOf(::ServiceViewModel)

    single<WorkManager> {
        Log.i("worker", "work manager created")
        WorkManager.getInstance(get())
    }

    single<SharedPreferences> {
        androidContext().getSharedPreferences(
            "saving_data",
            Context.MODE_PRIVATE
        )
    }
}