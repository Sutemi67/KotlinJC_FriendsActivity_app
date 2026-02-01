package apc.appcradle.kotlinjc_friendsactivity_app.data.network

import apc.appcradle.kotlinjc_friendsactivity_app.data.network.NetworkConstants.HOME_URL
import apc.appcradle.kotlinjc_friendsactivity_app.data.network.NetworkConstants.SERVER_URL
import apc.appcradle.kotlinjc_friendsactivity_app.domain.model.ApiRequestResult
import apc.appcradle.kotlinjc_friendsactivity_app.domain.model.network.requests.Requests
import apc.appcradle.kotlinjc_friendsactivity_app.domain.model.network.responses.Responses
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import java.net.SocketTimeoutException

class NetworkUtilsFunctions(
    val apiService: HttpClient
) {
    suspend inline fun <reified T : Responses> safePostRequest(
        endpoint: String,
        body: Requests,
        crossinline onSuccess: suspend (T) -> ApiRequestResult
    ): ApiRequestResult {
        return try {
            // Попытка 1: Основной сервер
            executeAndParse(SERVER_URL + endpoint, body, onSuccess)
        } catch (_: SocketTimeoutException) {
            try {
                // Попытка 2: Резервный сервер
                executeAndParse(HOME_URL + endpoint, body, onSuccess)
            } catch (e: Exception) {
                ApiRequestResult.Error("Connection error: ${e.message}")
            }
        }
    }

    suspend inline fun <reified T : Responses> executeAndParse(
        url: String,
        body: Requests,
        crossinline onSuccess: suspend (T) -> ApiRequestResult
    ): ApiRequestResult {
        val response = apiService.post(url) {
            contentType(ContentType.Application.Json)
            setBody(body)
        }
        return if (response.status.isSuccess()) {
            onSuccess(response.body<T>())
        } else {
            ApiRequestResult.Error(response.body<String?>())
        }
    }
}