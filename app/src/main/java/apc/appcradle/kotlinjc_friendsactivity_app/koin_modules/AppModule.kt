package apc.appcradle.kotlinjc_friendsactivity_app.koin_modules

import android.content.Context
import android.content.SharedPreferences
import apc.appcradle.kotlinjc_friendsactivity_app.MainViewModel
import apc.appcradle.kotlinjc_friendsactivity_app.data.SettingsStorageImpl
import apc.appcradle.kotlinjc_friendsactivity_app.data.StatsRepo
import apc.appcradle.kotlinjc_friendsactivity_app.data.TokenStorageImpl
import apc.appcradle.kotlinjc_friendsactivity_app.domain.NetworkClient
import apc.appcradle.kotlinjc_friendsactivity_app.domain.SettingsStorage
import apc.appcradle.kotlinjc_friendsactivity_app.permissions.PermissionManager
import apc.appcradle.kotlinjc_friendsactivity_app.sensors.AppSensorsManager
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    singleOf(::AppSensorsManager)
    singleOf(::PermissionManager)
    singleOf(::TokenStorageImpl)
    singleOf(::NetworkClient)
    singleOf(::StatsRepo)

    single<SettingsStorage> { SettingsStorageImpl(get()) }

    viewModelOf(::MainViewModel)

    single<SharedPreferences> {
        androidContext().getSharedPreferences(
            "settings",
            Context.MODE_PRIVATE
        )
    }
}