package apc.appcradle.kotlinjc_friendsactivity_app.koin_modules

import apc.appcradle.kotlinjc_friendsactivity_app.sensors.AppSensorsManager
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val appModule = module {
    singleOf(::AppSensorsManager)
}