package apc.appcradle.kotlinjc_friendsactivity_app.data.network

import apc.appcradle.kotlinjc_friendsactivity_app.data.configs.TokenRepositoryImpl
import apc.appcradle.kotlinjc_friendsactivity_app.data.network.NetworkConstants.GET_USER_DATA
import apc.appcradle.kotlinjc_friendsactivity_app.data.network.NetworkConstants.HOME_URL
import apc.appcradle.kotlinjc_friendsactivity_app.data.network.NetworkConstants.POST_ACTIVITY_HANDLE
import apc.appcradle.kotlinjc_friendsactivity_app.data.network.NetworkConstants.POST_USER_LOGIN_CHANGE_HANDLE
import apc.appcradle.kotlinjc_friendsactivity_app.data.network.NetworkConstants.POST_USER_LOGIN_HANDLE
import apc.appcradle.kotlinjc_friendsactivity_app.data.network.NetworkConstants.POST_USER_REGISTER_HANDLE
import apc.appcradle.kotlinjc_friendsactivity_app.data.network.NetworkConstants.SERVER_URL
import apc.appcradle.kotlinjc_friendsactivity_app.domain.model.ApiRequestResult
import apc.appcradle.kotlinjc_friendsactivity_app.domain.model.network.DataTransferState
import apc.appcradle.kotlinjc_friendsactivity_app.domain.model.network.requests.Requests
import apc.appcradle.kotlinjc_friendsactivity_app.domain.model.network.responses.Responses.LoginResponse
import apc.appcradle.kotlinjc_friendsactivity_app.domain.model.network.responses.Responses.RegisterResponse
import apc.appcradle.kotlinjc_friendsactivity_app.domain.model.network.responses.Responses.UserActivityResponse
import apc.appcradle.kotlinjc_friendsactivity_app.domain.model.network.responses.Responses.UserDataResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import java.net.ConnectException
import java.net.SocketTimeoutException

class NetworkClient(
    private val tokenRepositoryImpl: TokenRepositoryImpl,
    private val apiService: HttpClient,
    private val utils: NetworkUtilsFunctions
) {
    private fun saveToken(login: String, token: String) =
        tokenRepositoryImpl.saveToken(login = login, token = token)

    suspend fun sendRegistrationInfo(login: String, password: String): DataTransferState {
        val apiRequestResult = utils.safePostRequest<RegisterResponse>(
            endpoint = POST_USER_REGISTER_HANDLE,
            body = Requests.RegisterRequest(login, password),
            onSuccess = { response ->
                saveToken(login, response.token)
                ApiRequestResult.Success(result = DataTransferState(isSuccessful = true))
            })
        return when (apiRequestResult) {
            is ApiRequestResult.Success -> {
                apiRequestResult.result
            }

            is ApiRequestResult.Error -> {
                DataTransferState(errorMessage = apiRequestResult.message)
            }
        }
    }

    suspend fun sendLoginInfo(login: String, password: String): DataTransferState {
        val apiRequestResult = utils.safePostRequest<LoginResponse>(
            endpoint = POST_USER_LOGIN_HANDLE,
            body = Requests.LoginRequest(login, password),
            onSuccess = { response ->
                saveToken(login, response.token)
                ApiRequestResult.Success(result = DataTransferState(isSuccessful = true))
            }
        )
        return when (apiRequestResult) {
            is ApiRequestResult.Success -> {
                apiRequestResult.result
            }

            is ApiRequestResult.Error -> {
                DataTransferState(errorMessage = apiRequestResult.message)
            }
        }
    }

    suspend fun postUserDataAndSyncFriendsData(
        login: String,
        steps: Int,
        weeklySteps: Int
    ): UserActivityResponse {
        val body =
            Requests.UserActivityRequest(login = login, steps = steps, weeklySteps = weeklySteps)
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
        val body = Requests.LoginChangeRequest(login, newLogin)
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

    suspend fun getUserStepsData(login: String): UserDataResponse {
        return try {
            apiService.get(urlString = "$SERVER_URL$GET_USER_DATA/$login")
                .body<UserDataResponse>()
        } catch (e: Exception) {
            UserDataResponse(errorMessage = e.message)
        }
    }
}