package apc.appcradle.kotlinjc_friendsactivity_app.data

import apc.appcradle.kotlinjc_friendsactivity_app.domain.model.network.in_app_states.DataTransferStatus
import apc.appcradle.kotlinjc_friendsactivity_app.domain.model.network.requests.LoginChangeRequest
import apc.appcradle.kotlinjc_friendsactivity_app.domain.model.network.requests.RatingsRequest
import apc.appcradle.kotlinjc_friendsactivity_app.domain.model.network.requests.TokenRequest
import apc.appcradle.kotlinjc_friendsactivity_app.domain.model.network.responses.RatingsResponse
import apc.appcradle.kotlinjc_friendsactivity_app.domain.model.network.responses.TokenResponse
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

class NetworkClient(
    private val tokenRepositoryImpl: TokenRepositoryImpl
) {
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

    suspend fun sendRegistrationInfo(login: String, password: String): DataTransferStatus {
        val body = TokenRequest(login = login, password = password)
        return try {
            val response = networkService.post(urlString = "$serverUrl/register") {
                contentType(ContentType.Application.Json)
                setBody(body)
            }
            if (response.status.isSuccess()) {
                val token = response.body<TokenResponse>().token
                saveToken(login = login, token = token)
                DataTransferStatus(isLoading = false, true)
            } else {
                DataTransferStatus(isLoading = false, true, response.body<String?>())
            }
        } catch (_: SocketTimeoutException) {
            return try {
                val response = networkService.post(urlString = "$serverHomeUrl/register") {
                    contentType(ContentType.Application.Json)
                    setBody(body)
                }
                if (response.status.isSuccess()) {
                    val token = response.body<TokenResponse>().token
                    saveToken(login = login, token = token)
                    DataTransferStatus(isLoading = false, true)
                } else {
                    DataTransferStatus(isLoading = false, true, response.body<String?>())
                }
            } catch (e: Exception) {
                DataTransferStatus(isLoading = false, false, "${e.message}")
            }
        }
    }

    suspend fun sendLoginInfo(login: String, password: String): DataTransferStatus {
        val body = TokenRequest(login, password)
        return try {
            val response = networkService.post(urlString = "$serverUrl/login") {
                contentType(ContentType.Application.Json)
                setBody(body)
            }
            if (response.status.isSuccess()) {
                val token = response.body<TokenResponse>().token
                saveToken(login = login, token = token)
                DataTransferStatus(isLoading = false, true, errorMessage = null)
            } else {
                DataTransferStatus(isLoading = false, true, response.body<String?>())
            }
        } catch (_: SocketTimeoutException) {
            try {
                val response = networkService.post(urlString = "$serverHomeUrl/login") {
                    contentType(ContentType.Application.Json)
                    setBody(body)
                }
                if (response.status.isSuccess()) {
                    val token = response.body<TokenResponse>().token
                    saveToken(login = login, token = token)
                    DataTransferStatus(isLoading = false, true, errorMessage = null)
                } else {
                    DataTransferStatus(isLoading = false, true, response.body<String?>())
                }
            } catch (e: Exception) {
                DataTransferStatus(isLoading = false, false, "${e.message}")
            }
        } catch (e: HttpRequestTimeoutException) {
            DataTransferStatus(isLoading = false, false, "${e.message}")
        } catch (e: Exception) {
            DataTransferStatus(isLoading = false, false, "${e.message}")
        }
    }

    suspend fun postUserDataAndSyncFriendsData(
        login: String,
        steps: Int,
        weeklySteps: Int
    ): RatingsResponse {
        val body = RatingsRequest(login = login, steps = steps, weeklySteps = weeklySteps)
        return try {
            val request = networkService.post(urlString = "$serverUrl/post_activity") {
                contentType(ContentType.Application.Json)
                setBody(body)
            }
            if (request.status.isSuccess()) {
                val response = request.body<RatingsResponse>()
                RatingsResponse(response.friendsList, null, response.leader)
            } else {
                RatingsResponse(mutableListOf(), request.body<String?>(), null)
            }
        } catch (_: SocketTimeoutException) {
            try {
                val request = networkService.post(urlString = "$serverHomeUrl/post_activity") {
                    contentType(ContentType.Application.Json)
                    setBody(body)
                }
                if (request.status.isSuccess()) {
                    val response = request.body<RatingsResponse>()
                    RatingsResponse(response.friendsList, null, response.leader)
                } else {
                    RatingsResponse(mutableListOf(), request.body<String>(), null)
                }
            } catch (e: Exception) {
                RatingsResponse(
                    mutableListOf(),
                    "Не удалось подключиться к серверу. Проблема соединения.\n${e.message}",
                    null
                )
            }
        } catch (e: HttpRequestTimeoutException) {
            RatingsResponse(
                mutableListOf(),
                "За требуемое время сервер не ответил. Повторите попытку позже.\n${e.message}",
                null
            )
        } catch (e: ConnectException) {
            RatingsResponse(
                mutableListOf(),
                "Проблема связи. Возможно нет интернета.\n${e.message}",
                null
            )
        } catch (e: Exception) {
            RatingsResponse(
                mutableListOf(),
                "Connection error:\n${e.message}",
                null
            )
        }
    }

    suspend fun changeUserLogin(login: String, newLogin: String): Boolean {
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

//    suspend fun getUserData(login: String): UserActivity {
//        val request = networkService.get(urlString = serverUrl) {
//            contentType(ContentType.Application.Json)
//            setBody(login)
//        }
//        return if (request.status.isSuccess()) {
//            request.body<UserActivity>()
//        } else {
//            UserActivity()
//        }
//    }
}