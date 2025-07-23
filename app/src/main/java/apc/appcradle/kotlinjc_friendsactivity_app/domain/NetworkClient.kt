package apc.appcradle.kotlinjc_friendsactivity_app.domain

import android.util.Log
import apc.appcradle.kotlinjc_friendsactivity_app.domain.model.DataTransferState
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class NetworkClient(private val tokenStorage: TokenStorage) {

    companion object {
        @Serializable
        data class RegisterReceiveRemote(
            val login: String,
            val password: String
        )

        @Serializable
        data class RegisterResponseRemote(
            val token: String
        )

        @Serializable
        data class LoginReceiveRemote(
            val login: String,
            val password: String
        )

        @Serializable
        data class LoginResponseRemote(
            val token: String
        )
    }

    private val networkService = HttpClient(engineFactory = Android) {
        install(ContentNegotiation) {
            json(
                Json {
                    prettyPrint = true
                    isLenient = true
                }
            )
        }
        install(Auth) {
            bearer {
                loadTokens {
                    val token = tokenStorage.getToken()
                    if (token != null) {
                        BearerTokens(accessToken = token, refreshToken = "")
                    } else {
                        null
                    }
                }
                refreshTokens {
                    // This block is called when a 401 Unauthorized response is received.
                    // You can implement token refresh logic here if your API supports it.
                    // For now, we will just clear the tokens as there is no refresh token logic.
                    tokenStorage.clearToken()
                    null
                }
            }
        }
    }

    //    private val serverUrl = "http://127.0.0.1:5555"
    private val serverUrl = "http://212.3.131.67:5555/"

    private fun saveToken(token: String) {
        tokenStorage.saveToken(token)
    }

    fun clearToken() {
        tokenStorage.clearToken()
    }

    suspend fun sendRegistrationInfo(
        login: String,
        password: String
    ): DataTransferState {
        val body = RegisterReceiveRemote(login = login, password = password)

        return try {
            val response = networkService.post(urlString = "$serverUrl/register") {
                contentType(ContentType.Application.Json)
                setBody(body)
            }
            if (response.status.isSuccess()) {
                val token = response.body<RegisterResponseRemote>().token
                saveToken(token)
                Log.d(
                    "dataTransfer",
                    "сохраненный токен: ${tokenStorage.getToken()}\nвыданный токен: $token"
                )
                DataTransferState(true)
            } else {
                Log.e("dataTransfer", "${response.body<String?>()}")
                DataTransferState(true, response.body<String?>())
            }
        } catch (e: Exception) {
            Log.e("dataTransfer", "not successful sending", e)
            DataTransferState(false, "Connection error")
        }
    }


    suspend fun sendLoginInfo(
        login: String,
        password: String
    ): DataTransferState {
        val body = LoginReceiveRemote(login, password)
        return try {
            val response = networkService.post(urlString = "$serverUrl/login") {
                contentType(ContentType.Application.Json)
                setBody(body)
            }
            if (response.status.isSuccess()) {
                val token = response.body<LoginResponseRemote>().token
                saveToken(token)
                Log.d(
                    "dataTransfer",
                    "сохраненный токен: ${tokenStorage.getToken()}\nвыданный токен: $token"
                )
                DataTransferState(true, errorMessage = null)
            } else {
                Log.e("dataTransfer", "${response.body<String?>()}")
                DataTransferState(true, response.body<String?>())
            }
        } catch (e: Exception) {
            Log.e("dataTransfer", "not successful sending", e)
            DataTransferState(false, "Connection error")
        }
    }


    // Example of a request to a protected endpoint
    suspend fun getSomeProtectedData(): String? {
        return try {
            // The Auth plugin will automatically add the "Authorization: Bearer ..." header
            networkService.get(urlString = "$serverUrl/some_protected_route").body()
        } catch (e: Exception) {
            Log.e("dataTransfer", "not successful getting protected data", e)
            null
        }
    }
}

