package apc.appcradle.kotlinjc_friendsactivity_app.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.work.WorkManager
import apc.appcradle.kotlinjc_friendsactivity_app.MainViewModel
import apc.appcradle.kotlinjc_friendsactivity_app.data.SensorsManager
import apc.appcradle.kotlinjc_friendsactivity_app.data.SettingsRepositoryImpl
import apc.appcradle.kotlinjc_friendsactivity_app.data.StatsRepository
import apc.appcradle.kotlinjc_friendsactivity_app.data.TokenRepositoryImpl
import apc.appcradle.kotlinjc_friendsactivity_app.data.NetworkClient
import apc.appcradle.kotlinjc_friendsactivity_app.services.PermissionManager
import apc.appcradle.kotlinjc_friendsactivity_app.domain.SettingsRepository
import apc.appcradle.kotlinjc_friendsactivity_app.domain.TokenRepository
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val koinAppModule = module {
    singleOf(::SettingsRepositoryImpl).bind<SettingsRepository>()
    singleOf(::TokenRepositoryImpl).bind<TokenRepository>()
    singleOf(::SensorsManager)
    singleOf(::PermissionManager)
    singleOf(::NetworkClient)
    singleOf(::StatsRepository)

    viewModelOf(::MainViewModel)

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