package apc.appcradle.kotlinjc_friendsactivity_app.network

import apc.appcradle.kotlinjc_friendsactivity_app.features.auth.TokenRepository
import apc.appcradle.kotlinjc_friendsactivity_app.network.NetworkConstants.GET_USER_DATA
import apc.appcradle.kotlinjc_friendsactivity_app.network.NetworkConstants.POST_ACTIVITY_HANDLE
import apc.appcradle.kotlinjc_friendsactivity_app.network.NetworkConstants.POST_USER_LOGIN_CHANGE_HANDLE
import apc.appcradle.kotlinjc_friendsactivity_app.network.NetworkConstants.POST_USER_LOGIN_HANDLE
import apc.appcradle.kotlinjc_friendsactivity_app.network.NetworkConstants.POST_USER_REGISTER_HANDLE
import apc.appcradle.kotlinjc_friendsactivity_app.network.model.ApiRequestResult
import apc.appcradle.kotlinjc_friendsactivity_app.network.model.DataTransferState
import apc.appcradle.kotlinjc_friendsactivity_app.network.model.Requests
import apc.appcradle.kotlinjc_friendsactivity_app.network.model.Responses
import apc.appcradle.kotlinjc_friendsactivity_app.network.model.Responses.LoginResponse
import apc.appcradle.kotlinjc_friendsactivity_app.network.model.Responses.RegisterResponse
import apc.appcradle.kotlinjc_friendsactivity_app.network.model.Responses.UserActivityResponse
import apc.appcradle.kotlinjc_friendsactivity_app.network.model.Responses.UserDataResponse
import apc.appcradle.kotlinjc_friendsactivity_app.core.utils.LoggerType
import apc.appcradle.kotlinjc_friendsactivity_app.core.utils.logger

class NetworkClient(
    private val tokenRepository: TokenRepository,
    private val utils: NetworkUtilsFunctions
) {
    private suspend fun saveToken(login: String, token: String) =
        tokenRepository.saveToken(login = login, token = token)

    suspend fun sendRegistrationInfo(login: String, password: String): DataTransferState {
        val apiRequestResult = utils.safeRequest<RegisterResponse>(
            endpoint = POST_USER_REGISTER_HANDLE,
            body = Requests.RegisterRequest(login, password),
            type = RequestsType.POST,
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
        val apiRequestResult = utils.safeRequest<LoginResponse>(
            endpoint = POST_USER_LOGIN_HANDLE,
            body = Requests.LoginRequest(login, password),
            type = RequestsType.POST,
            onSuccess = { response ->
                saveToken(login, response.token)
                ApiRequestResult.Success(
                    result = DataTransferState(isSuccessful = true)
                )
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
        val apiRequestResult = utils.safeRequest<UserActivityResponse>(
            endpoint = POST_ACTIVITY_HANDLE,
            body = Requests.UserActivityRequest(login, steps, weeklySteps),
            type = RequestsType.POST,
            onSuccess = { response ->
                ApiRequestResult.Success(
                    result = DataTransferState(
                        isSuccessful = true,
                        response = response
                    )
                )
            }
        )
        return when (apiRequestResult) {
            is ApiRequestResult.Success -> apiRequestResult.result.response as UserActivityResponse

            is ApiRequestResult.Error -> UserActivityResponse(
                mutableListOf(),
                apiRequestResult.message,
                null
            )
        }
    }

    suspend fun changeUserLogin(login: String, newLogin: String): Boolean {
        val apiRequestResult = utils.safeRequest<Responses.LoginChangeResponse>(
            endpoint = POST_USER_LOGIN_CHANGE_HANDLE,
            body = Requests.LoginChangeRequest(login, newLogin),
            type = RequestsType.POST,
            onSuccess = { response ->
                ApiRequestResult.Success(
                    result = DataTransferState(
                        isSuccessful = true,
                        response = response
                    )
                )
            }
        )
        return when (apiRequestResult) {
            is ApiRequestResult.Success -> true
            is ApiRequestResult.Error -> {
                logger(LoggerType.Error, "${apiRequestResult.message}")
                false
            }
        }
    }

    suspend fun getUserStepsData(login: String): UserDataResponse {
        val apiRequestResult = utils.safeRequest<UserDataResponse>(
            endpoint = "$GET_USER_DATA/$login",
            body = Requests.UserActivityRequest(login, 0, 0),
            type = RequestsType.GET,
            onSuccess = { response ->
                ApiRequestResult.Success(
                    result = DataTransferState(
                        isSuccessful = true,
                        response = response
                    )
                )
            }
        )
        return when (apiRequestResult) {
            is ApiRequestResult.Success -> apiRequestResult.result.response as UserDataResponse

            is ApiRequestResult.Error -> UserDataResponse(
                null,
                null,
                apiRequestResult.message
            )
        }
    }
}