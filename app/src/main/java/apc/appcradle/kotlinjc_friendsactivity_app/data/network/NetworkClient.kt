package apc.appcradle.kotlinjc_friendsactivity_app.data.network

import apc.appcradle.kotlinjc_friendsactivity_app.data.configs.TokenRepositoryImpl
import apc.appcradle.kotlinjc_friendsactivity_app.domain.model.network.DataTransferState
import apc.appcradle.kotlinjc_friendsactivity_app.domain.model.network.requests.LoginChangeRequest
import apc.appcradle.kotlinjc_friendsactivity_app.domain.model.network.requests.LoginRequest
import apc.appcradle.kotlinjc_friendsactivity_app.domain.model.network.requests.RegisterRequest
import apc.appcradle.kotlinjc_friendsactivity_app.domain.model.network.requests.UserActivityRequest
import apc.appcradle.kotlinjc_friendsactivity_app.domain.model.network.responses.LoginResponse
import apc.appcradle.kotlinjc_friendsactivity_app.domain.model.network.responses.RegisterResponse
import apc.appcradle.kotlinjc_friendsactivity_app.domain.model.network.responses.UserActivityResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import java.net.ConnectException
import java.net.SocketTimeoutException

class NetworkClient(
    private val tokenRepositoryImpl: TokenRepositoryImpl,
    private val apiService: HttpClient
) {
//    private val networkService = HttpClient(engineFactory = Android) {
//        install(HttpTimeout) {
//            requestTimeoutMillis = 4000
//            connectTimeoutMillis = 4000
//            socketTimeoutMillis = 4000
//        }
//        install(ContentNegotiation) {
//            json(
//                Json {
//                    prettyPrint = true
//                    isLenient = true
//                    ignoreUnknownKeys = true
//                }
//            )
//        }
//        install(Auth) {
//            bearer {
//                loadTokens {
//                    val token = tokenRepositoryImpl.getToken()
//                    if (token != null) {
//                        BearerTokens(accessToken = token, refreshToken = "")
//                    } else {
//                        null
//                    }
//                }
//                refreshTokens {
//                    tokenRepositoryImpl.clearToken()
//                    null
//                }
//            }
//        }
//    }

    private fun saveToken(login: String, token: String) =
        tokenRepositoryImpl.saveToken(login = login, token = token)

    suspend fun sendRegistrationInfo(login: String, password: String): DataTransferState {
        val body = RegisterRequest(login = login, password = password)
        return try {
            val response =
                apiService.post(urlString = "$SERVER_URL$POST_USER_REGISTER_HANDLE") {
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
                val response =
                    apiService.post(urlString = "$HOME_URL$POST_USER_REGISTER_HANDLE") {
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
            } catch (e: Exception) {
                DataTransferState(isLoading = false, false, "${e.message}")
            }
        }
    }

    suspend fun sendLoginInfo(login: String, password: String): DataTransferState {
        val body = LoginRequest(login, password)
        return try {
            val response = apiService.post(urlString = "$SERVER_URL$POST_USER_LOGIN_HANDLE") {
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
                val response = apiService.post(urlString = "$HOME_URL$POST_USER_LOGIN_HANDLE") {
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

    suspend fun postUserDataAndSyncFriendsData(
        login: String,
        steps: Int,
        weeklySteps: Int
    ): UserActivityResponse {
        val body = UserActivityRequest(login = login, steps = steps, weeklySteps = weeklySteps)
        return try {
            val request = apiService.post(urlString = "$SERVER_URL$POST_ACTIVITY_HANDLE") {
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
                val request = apiService.post(urlString = "$HOME_URL$POST_ACTIVITY_HANDLE") {
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
        val body = LoginChangeRequest(login, newLogin)
        try {
            val response =
                apiService.post(urlString = "$SERVER_URL$POST_USER_LOGIN_CHANGE_HANDLE") {
                    contentType(ContentType.Application.Json)
                    setBody(body)
                }
            return response.status.isSuccess()
        } catch (_: Exception) {
            return false
        }
    }

    companion object {
        const val SERVER_URL = "http://212.3.131.67:6655/"
        const val HOME_URL = "http://192.168.1.100:6655/"
        const val POST_USER_LOGIN_CHANGE_HANDLE = "/login_update"
        const val POST_USER_REGISTER_HANDLE = "/register"
        const val POST_USER_LOGIN_HANDLE = "/login"
        const val POST_ACTIVITY_HANDLE = "/post_activity"
    }
}