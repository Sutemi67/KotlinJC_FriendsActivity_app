package apc.appcradle.kotlinjc_friendsactivity_app.core.di

import android.content.Context
import android.content.SharedPreferences
import androidx.work.WorkManager
import apc.appcradle.kotlinjc_friendsactivity_app.core.models.ISettingsRepository
import apc.appcradle.kotlinjc_friendsactivity_app.core.models.ITokenRepository
import apc.appcradle.kotlinjc_friendsactivity_app.core.services.AppSensorsManager
import apc.appcradle.kotlinjc_friendsactivity_app.core.services.PermissionManager
import apc.appcradle.kotlinjc_friendsactivity_app.features.AppStateManager
import apc.appcradle.kotlinjc_friendsactivity_app.features.auth.AuthViewModel
import apc.appcradle.kotlinjc_friendsactivity_app.features.auth.TokenRepository
import apc.appcradle.kotlinjc_friendsactivity_app.features.main.MainViewModel
import apc.appcradle.kotlinjc_friendsactivity_app.features.main.StatsRepository
import apc.appcradle.kotlinjc_friendsactivity_app.features.ratings.RatingsViewModel
import apc.appcradle.kotlinjc_friendsactivity_app.features.settings.SettingsRepository
import apc.appcradle.kotlinjc_friendsactivity_app.features.settings.SettingsViewModel
import apc.appcradle.kotlinjc_friendsactivity_app.network.NetworkClient
import apc.appcradle.kotlinjc_friendsactivity_app.network.NetworkUtilsFunctions
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val koinAppModule = module {
    singleOf(::SettingsRepository).bind<ISettingsRepository>()
    singleOf(::TokenRepository).bind<ITokenRepository>()
    singleOf(::AppSensorsManager)
    singleOf(::PermissionManager)
    singleOf(::NetworkClient)
    singleOf(::StatsRepository)
    singleOf(::NetworkUtilsFunctions)
    singleOf(::AppStateManager)

    viewModelOf(::MainViewModel)
    viewModelOf(::SettingsViewModel)
    viewModelOf(::AuthViewModel)
    viewModelOf(::RatingsViewModel)

    single<WorkManager> { WorkManager.getInstance(get()) }

    single<SharedPreferences> {
        androidContext().getSharedPreferences(
            "saving_data",
            Context.MODE_PRIVATE
        )
    }
    single<HttpClient> {
        HttpClient(engineFactory = Android) {
            install(HttpTimeout) {
                requestTimeoutMillis = 4000
                connectTimeoutMillis = 4000
                socketTimeoutMillis = 4000
            }
            install(ContentNegotiation) {
                json(
                    Json {
                        prettyPrint = true
                        isLenient = true
                        ignoreUnknownKeys = true
                    }
                )
            }
            install(Auth) {
                bearer {
                    loadTokens {
                        val tokenRepo = get<ITokenRepository>()
                        tokenRepo.getToken()?.let { token ->
                            BearerTokens(accessToken = token, refreshToken = "")
                        }
                    }
                    refreshTokens {
                        val tokenRepo = get<ITokenRepository>()
                        tokenRepo.clearToken()
                        null
                    }
                }
            }
        }
    }
}