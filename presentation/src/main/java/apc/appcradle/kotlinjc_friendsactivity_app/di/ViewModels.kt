package apc.appcradle.kotlinjc_friendsactivity_app.di

import apc.appcradle.kotlinjc_friendsactivity_app.presentation.view_models.NetworkViewModel
import apc.appcradle.kotlinjc_friendsactivity_app.presentation.view_models.ServiceViewModel
import apc.appcradle.kotlinjc_friendsactivity_app.presentation.view_models.SettingsViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val koinViewModels = module {

    //viewModels
    viewModelOf(::NetworkViewModel)
    viewModelOf(::SettingsViewModel)
    viewModelOf(::ServiceViewModel)
}