package apc.appcradle.kotlinjc_friendsactivity_app.domain

import android.util.Log
import apc.appcradle.kotlinjc_friendsactivity_app.data.TokenRepositoryImpl
import apc.appcradle.kotlinjc_friendsactivity_app.domain.model.network.DataTransferState
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.net.ConnectException
import java.net.SocketTimeoutException

class NetworkClient(
    private val tokenRepositoryImpl: TokenRepositoryImpl
) {

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

        @Serializable
        data class UserActivity(
            val login: String,
            val steps: Int,
            val weeklySteps: Int
        )

        @Serializable
        data class UserActivityResponse(
            val friendsList: MutableList<UserActivity>,
            val errorMessage: String?,
            val leader: String?
        )

        @Serializable
        data class LoginChange(
            val login: String,
            val newLogin: String
        )
    }

    private val networkService = HttpClient(engineFactory = Android) {
        install(HttpTimeout) {
            requestTimeoutMillis = 5000
            connectTimeoutMillis = 5000
            socketTimeoutMillis = 5000
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
                    val token = tokenRepositoryImpl.getToken()
                    if (token != null) {
                        BearerTokens(accessToken = token, refreshToken = "")
                    } else {
                        null
                    }
                }
                refreshTokens {
                    tokenRepositoryImpl.clearToken()
                    null
                }
            }
        }
    }

    private val serverUrl = "http://212.3.131.67:6655/"
    private val serverHomeUrl = "http://192.168.1.100:6655/"

    private fun saveToken(login: String, token: String) {
        tokenRepositoryImpl.saveToken(login = login, token = token)
    }

    suspend fun sendRegistrationInfo(login: String, password: String): DataTransferState {
        val body = RegisterReceiveRemote(login = login, password = password)
        return try {
            val response = networkService.post(urlString = "$serverUrl/register") {
                contentType(ContentType.Application.Json)
                setBody(body)
            }
            if (response.status.isSuccess()) {
                val token = response.body<RegisterResponseRemote>().token
                saveToken(login = login, token = token)
                Log.d(
                    "dataTransfer",
                    "сохраненный токен: ${tokenRepositoryImpl.getToken()}\nвыданный токен: $token"
                )
                DataTransferState(isLoading = false, true)
            } else {
                Log.e("dataTransfer", "${response.body<String?>()}")
                DataTransferState(isLoading = false, true, response.body<String?>())
            }
        } catch (_: SocketTimeoutException) {
            return try {
                val response = networkService.post(urlString = "$serverHomeUrl/register") {
                    contentType(ContentType.Application.Json)
                    setBody(body)
                }
                if (response.status.isSuccess()) {
                    val token = response.body<RegisterResponseRemote>().token
                    saveToken(login = login, token = token)
                    Log.d(
                        "dataTransfer",
                        "сохраненный токен: ${tokenRepositoryImpl.getToken()}\nвыданный токен: $token"
                    )
                    DataTransferState(isLoading = false, true)
                } else {
                    Log.e("dataTransfer", "${response.body<String?>()}")
                    DataTransferState(isLoading = false, true, response.body<String?>())
                }
            } catch (e: Exception) {
                Log.e("dataTransfer", "not successful sending", e)
                DataTransferState(isLoading = false, false, "${e.message}")
            }
        }
    }

    suspend fun sendLoginInfo(login: String, password: String): DataTransferState {
        val body = LoginReceiveRemote(login, password)
        return try {
            val response = networkService.post(urlString = "$serverUrl/login") {
                contentType(ContentType.Application.Json)
                setBody(body)
            }
            if (response.status.isSuccess()) {
                val token = response.body<LoginResponseRemote>().token
                saveToken(login = login, token = token)
                Log.d(
                    "dataTransfer",
                    "сохраненный токен: ${tokenRepositoryImpl.getToken()}\nвыданный токен: $token"
                )
                DataTransferState(isLoading = false, true, errorMessage = null)
            } else {
                Log.e("dataTransfer", "${response.body<String?>()}")
                DataTransferState(isLoading = false, true, response.body<String?>())
            }
        } catch (_: SocketTimeoutException) {
            try {
                val response = networkService.post(urlString = "$serverHomeUrl/login") {
                    contentType(ContentType.Application.Json)
                    setBody(body)
                }
                if (response.status.isSuccess()) {
                    val token = response.body<LoginResponseRemote>().token
                    saveToken(login = login, token = token)
                    Log.d(
                        "dataTransfer",
                        "сохраненный токен: ${tokenRepositoryImpl.getToken()}\nвыданный токен: $token"
                    )
                    DataTransferState(isLoading = false, true, errorMessage = null)
                } else {
                    Log.e("dataTransfer", "${response.body<String?>()}")
                    DataTransferState(isLoading = false, true, response.body<String?>())
                }
            } catch (e: Exception) {
                Log.e("dataTransfer", "error is - ${e.message}")
                DataTransferState(
                    isLoading = false,
                    false,
                    "Connection error: server does not respond"
                )
            }
        } catch (e: HttpRequestTimeoutException) {
            Log.e("dataTransfer", "error is - ${e.message}")
            DataTransferState(isLoading = false, false, "Request timeout has expired")
        } catch (e: Exception) {
            Log.e("dataTransfer", "error is - ${e.message}")
            DataTransferState(isLoading = false, false, "Unknown error")
        }
    }

    suspend fun postUserDataAndSyncFriendsData(
        login: String,
        steps: Int,
        weeklySteps: Int
    ): UserActivityResponse {
        val body = UserActivity(login = login, steps = steps, weeklySteps = weeklySteps)
        return try {
            val request = networkService.post(urlString = "$serverUrl/post_activity") {
                contentType(ContentType.Application.Json)
                setBody(body)
            }
            if (request.status.isSuccess()) {
                val response = request.body<UserActivityResponse>()
                UserActivityResponse(response.friendsList, null, response.leader)
            } else {
                UserActivityResponse(mutableListOf(), request.body<String?>(), null)
            }
        } catch (_: SocketTimeoutException) {
            try {
                val request = networkService.post(urlString = "$serverHomeUrl/post_activity") {
                    contentType(ContentType.Application.Json)
                    setBody(body)
                }
                if (request.status.isSuccess()) {
                    val response = request.body<UserActivityResponse>()
                    UserActivityResponse(response.friendsList, null, response.leader)
                } else {
                    UserActivityResponse(mutableListOf(), request.body<String>(), null)
                }
            } catch (e: Exception) {
                UserActivityResponse(
                    mutableListOf(),
                    "Не удалось подключиться к серверу. Проблема соединения.\n${e.message}",
                    null
                )
            }
        } catch (e: HttpRequestTimeoutException) {
            UserActivityResponse(
                mutableListOf(),
                "За требуемое время сервер не ответил. Повторите попытку позже.\n${e.message}",
                null
            )
        } catch (e: ConnectException) {
            UserActivityResponse(
                mutableListOf(),
                "Проблема связи. Возможно нет интернета.\n${e.message}",
                null
            )
        } catch (e: Exception) {
            UserActivityResponse(
                mutableListOf(),
                "Connection error:\n${e.message}",
                null
            )
        }
    }

    suspend fun changeUserLogin(login: String, newLogin: String): Boolean {
        val body = LoginChange(login, newLogin)
        try {
            val response = networkService.post(urlString = "$serverUrl/login_update") {
                contentType(ContentType.Application.Json)
                setBody(body)
            }
            if (response.status.isSuccess()) {
                Log.d(
                    "dataTransfer",
                    "network client -> login change successful, ${response.status}"
                )
                return true
            }
            Log.e(
                "dataTransfer",
                "network client -> login change unsuccessful, ${response.status}"
            )
            return false
        } catch (e: Exception) {
            Log.e("dataTransfer", "network client -> not successful sending", e)
            return false
        }
    }
}