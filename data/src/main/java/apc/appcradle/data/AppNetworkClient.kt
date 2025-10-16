package apc.appcradle.data

import apc.appcradle.domain.NetworkClient
import apc.appcradle.domain.models.network.DataTransferState
import apc.appcradle.domain.models.network.requests.LoginChangeRequest
import apc.appcradle.domain.models.network.requests.LoginRequest
import apc.appcradle.domain.models.network.requests.RegisterRequest
import apc.appcradle.domain.models.network.requests.UserActivityRequest
import apc.appcradle.domain.models.network.responses.LoginResponse
import apc.appcradle.domain.models.network.responses.RegisterResponse
import apc.appcradle.domain.models.network.responses.UserActivityResponse
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
import kotlinx.serialization.json.Json
import java.net.ConnectException
import java.net.SocketTimeoutException

class AppNetworkClient(
    private val appTokenRepository: AppTokenRepository
) : NetworkClient {
    private val networkService = HttpClient(engineFactory = Android) {
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
                    val token = appTokenRepository.getToken()
                    if (token != null) {
                        BearerTokens(accessToken = token, refreshToken = "")
                    } else {
                        null
                    }
                }
                refreshTokens {
                    appTokenRepository.clearToken()
                    null
                }
            }
        }
    }

    private val serverUrl = "http://212.3.131.67:6655/"
    private val serverHomeUrl = "http://192.168.1.100:6655/"

    override fun saveToken(login: String, token: String) {
        appTokenRepository.saveToken(login = login, token = token)
    }

    override suspend fun sendRegistrationInfo(login: String, password: String): DataTransferState {
        val body = RegisterRequest(login = login, password = password)
        return try {
            val response = networkService.post(urlString = "$serverUrl/register") {
                contentType(ContentType.Application.Json)
                setBody(body)
            }
            if (response.status.isSuccess()) {
                val token = response.body<RegisterResponse>().token
                saveToken(login = login, token = token)
                DataTransferState(isLoading = false, true)
            } else {
                DataTransferState(isLoading = false, true, response.body<String?>())
            }
        } catch (_: SocketTimeoutException) {
            return try {
                val response = networkService.post(urlString = "$serverHomeUrl/register") {
                    contentType(ContentType.Application.Json)
                    setBody(body)
                }
                if (response.status.isSuccess()) {
                    val token = response.body<RegisterResponse>().token
                    saveToken(login = login, token = token)
                    DataTransferState(isLoading = false, true, errorMessage = null)
                } else {
                    DataTransferState(isLoading = false, true, response.body<String?>())
                }
            } catch (e: Exception) {
                DataTransferState(isLoading = false, false, "${e.message}")
            }
        }
    }

    override suspend fun sendLoginInfo(login: String, password: String): DataTransferState {
        val body = LoginRequest(login, password)
        return try {
            val response = networkService.post(urlString = "$serverUrl/login") {
                contentType(ContentType.Application.Json)
                setBody(body)
            }
            if (response.status.isSuccess()) {
                val token = response.body<LoginResponse>().token
                saveToken(login = login, token = token)
                DataTransferState(isLoading = false, true, errorMessage = null)
            } else {
                DataTransferState(isLoading = false, true, response.body<String?>())
            }
        } catch (_: SocketTimeoutException) {
            try {
                val response = networkService.post(urlString = "$serverHomeUrl/login") {
                    contentType(ContentType.Application.Json)
                    setBody(body)
                }
                if (response.status.isSuccess()) {
                    val token = response.body<LoginResponse>().token
                    saveToken(login = login, token = token)
                    DataTransferState(isLoading = false, true, errorMessage = null)
                } else {
                    DataTransferState(isLoading = false, true, response.body<String?>())
                }
            } catch (_: Exception) {
                DataTransferState(
                    isLoading = false,
                    false,
                    "Connection error: server does not respond"
                )
            }
        } catch (_: HttpRequestTimeoutException) {
            DataTransferState(isLoading = false, false, "Request timeout has expired")
        } catch (_: Exception) {
            DataTransferState(isLoading = false, false, "Unknown error")
        }
    }

    override suspend fun postUserDataAndSyncFriendsData(
        login: String,
        steps: Int,
        weeklySteps: Int
    ): UserActivityResponse {
        val body = UserActivityRequest(login = login, steps = steps, weeklySteps = weeklySteps)
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

    override suspend fun changeUserLogin(login: String, newLogin: String): Boolean {
        val body = LoginChangeRequest(login, newLogin)
        try {
            val response = networkService.post(urlString = "$serverUrl/login_update") {
                contentType(ContentType.Application.Json)
                setBody(body)
            }
            return response.status.isSuccess()
        } catch (_: Exception) {
            return false
        }
    }
}