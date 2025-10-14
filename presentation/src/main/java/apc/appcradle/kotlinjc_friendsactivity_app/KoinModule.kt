package apc.appcradle.kotlinjc_friendsactivity_app

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.work.WorkManager
import apc.appcradle.data.NetworkClient
import apc.appcradle.data.StatsRepository
import apc.appcradle.data.TokenRepositoryImpl
import apc.appcradle.data.TrancateWorker
import apc.appcradle.domain.SettingsRepository
import apc.appcradle.domain.TokenRepository
import apc.appcradle.data.SensorsManager
import apc.appcradle.data.SettingsRepositoryImpl
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
    singleOf(::TrancateWorker)

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