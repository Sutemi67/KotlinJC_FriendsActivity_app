package apc.appcradle.kotlinjc_friendsactivity_app.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.work.WorkManager
import apc.appcradle.kotlinjc_friendsactivity_app.MainViewModel
import apc.appcradle.kotlinjc_friendsactivity_app.data.configs.SettingsRepositoryImpl
import apc.appcradle.kotlinjc_friendsactivity_app.data.configs.TokenRepositoryImpl
import apc.appcradle.kotlinjc_friendsactivity_app.data.network.NetworkClient
import apc.appcradle.kotlinjc_friendsactivity_app.data.network.NetworkUtilsFunctions
import apc.appcradle.kotlinjc_friendsactivity_app.data.steps_data.AppSensorsManager
import apc.appcradle.kotlinjc_friendsactivity_app.data.steps_data.StatsRepository
import apc.appcradle.kotlinjc_friendsactivity_app.domain.SettingsRepository
import apc.appcradle.kotlinjc_friendsactivity_app.domain.TokenRepository
import apc.appcradle.kotlinjc_friendsactivity_app.services.PermissionManager
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
    singleOf(::SettingsRepositoryImpl).bind<SettingsRepository>()
    singleOf(::TokenRepositoryImpl).bind<TokenRepository>()
    singleOf(::AppSensorsManager)
    singleOf(::PermissionManager)
    singleOf(::NetworkClient)
    singleOf(::StatsRepository)
    singleOf(::NetworkUtilsFunctions)

    viewModelOf(::MainViewModel)

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
                        val tokenRepo = get<TokenRepository>()
                        tokenRepo.getToken()?.let { token ->
                            BearerTokens(accessToken = token, refreshToken = "")
                        }
                    }
                    refreshTokens {
                        val tokenRepo = get<TokenRepository>()
                        tokenRepo.clearToken()
                        null
                    }
                }
            }
        }
    }
}