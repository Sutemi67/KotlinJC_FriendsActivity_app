package apc.appcradle.kotlinjc_friendsactivity_app.di

import apc.appcradle.domain.usecases_auth.ChangeLoginUseCase
import apc.appcradle.domain.usecases_auth.CheckPermanentAuthUseCase
import apc.appcradle.domain.usecases_auth.GetTokenUseCase
import apc.appcradle.domain.usecases_auth.LogoutUseCase
import apc.appcradle.domain.usecases_auth.OfflineUseUseCase
import apc.appcradle.domain.usecases_auth.SaveNewLoginUseCase
import apc.appcradle.domain.usecases_auth.SendLoginUseCase
import apc.appcradle.domain.usecases_auth.SendRegistrationUseCase
import apc.appcradle.domain.usecases_auth.SyncDataUseCase
import apc.appcradle.domain.usecases_sensors.GetStepsUseCase
import apc.appcradle.domain.usecases_sensors.RegisterSensorsUseCase
import apc.appcradle.domain.usecases_sensors.UnregisterSensorsUseCase
import apc.appcradle.domain.usecases_service.CheckServiceStatusUseCase
import apc.appcradle.domain.usecases_service.StartServiceUseCase
import apc.appcradle.domain.usecases_service.StopServiceUseCase
import apc.appcradle.domain.usecases_settings.LoadSettingsUseCase
import apc.appcradle.domain.usecases_settings.SaveSettingsUseCase
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val koinUseCases = module {
    //UseCases
    singleOf(::SaveSettingsUseCase)
    singleOf(::LoadSettingsUseCase)

    singleOf(::CheckServiceStatusUseCase)
    singleOf(::StartServiceUseCase)
    singleOf(::StopServiceUseCase)

    singleOf(::GetStepsUseCase)
    singleOf(::RegisterSensorsUseCase)
    singleOf(::UnregisterSensorsUseCase)

    singleOf(::ChangeLoginUseCase)
    singleOf(::CheckPermanentAuthUseCase)
    singleOf(::GetTokenUseCase)
    singleOf(::LogoutUseCase)
    singleOf(::OfflineUseUseCase)
    singleOf(::SaveNewLoginUseCase)
    singleOf(::SendLoginUseCase)
    singleOf(::SendRegistrationUseCase)
    singleOf(::SyncDataUseCase)
}