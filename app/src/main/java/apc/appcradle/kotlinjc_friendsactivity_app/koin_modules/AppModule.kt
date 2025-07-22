package apc.appcradle.kotlinjc_friendsactivity_app.koin_modules

import apc.appcradle.kotlinjc_friendsactivity_app.MainViewModel
import apc.appcradle.kotlinjc_friendsactivity_app.domain.NetworkClient
import apc.appcradle.kotlinjc_friendsactivity_app.permissions.PermissionManager
import apc.appcradle.kotlinjc_friendsactivity_app.sensors.AppSensorsManager
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    singleOf(::AppSensorsManager)
    singleOf(::PermissionManager)
    singleOf(::NetworkClient)
    viewModelOf(::MainViewModel)
}