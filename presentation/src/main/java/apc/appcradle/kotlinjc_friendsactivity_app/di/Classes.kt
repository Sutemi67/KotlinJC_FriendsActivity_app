package apc.appcradle.kotlinjc_friendsactivity_app.di

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.work.WorkManager
import apc.appcradle.data.AppNetworkClient
import apc.appcradle.data.AppSensorsManager
import apc.appcradle.data.AppSettingsRepository
import apc.appcradle.data.AppStatsRepository
import apc.appcradle.data.AppTokenRepository
import apc.appcradle.domain.NetworkClient
import apc.appcradle.domain.SensorsManager
import apc.appcradle.domain.SettingsRepository
import apc.appcradle.domain.StatsRepository
import apc.appcradle.domain.TokenRepository
import apc.appcradle.kotlinjc_friendsactivity_app.PermissionManager
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val koinClasses = module {

    //Classes
    singleOf(::PermissionManager)
    singleOf(::AppSettingsRepository).bind<SettingsRepository>()
    singleOf(::AppTokenRepository).bind<TokenRepository>()
    singleOf(::AppSensorsManager).bind<SensorsManager>()
    singleOf(::AppNetworkClient).bind<NetworkClient>()
    singleOf(::AppStatsRepository).bind<StatsRepository>()

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