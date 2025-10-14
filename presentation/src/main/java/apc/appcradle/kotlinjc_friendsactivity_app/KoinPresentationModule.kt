package apc.appcradle.kotlinjc_friendsactivity_app

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.work.WorkManager
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val koinPresentationModule = module {
    singleOf(::SettingsRepositoryImpl).bind<SettingsRepository>()
    singleOf(::TokenRepositoryImpl).bind<TokenRepository>()
    singleOf(::SensorsManager)
    singleOf(::PermissionManager)
    singleOf(::NetworkClient)
    singleOf(::StatsRepository)
    singleOf(::TrancateWorker)

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