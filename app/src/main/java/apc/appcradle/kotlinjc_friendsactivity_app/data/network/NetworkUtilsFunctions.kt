package apc.appcradle.kotlinjc_friendsactivity_app.data.network

import apc.appcradle.kotlinjc_friendsactivity_app.data.network.NetworkConstants.HOME_URL
import apc.appcradle.kotlinjc_friendsactivity_app.data.network.NetworkConstants.SERVER_URL
import apc.appcradle.kotlinjc_friendsactivity_app.data.network.model.ApiRequestResult
import apc.appcradle.kotlinjc_friendsactivity_app.data.network.model.Requests
import apc.appcradle.kotlinjc_friendsactivity_app.data.network.model.Responses
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess

enum class RequestsType { GET, POST }

class NetworkUtilsFunctions(
    val apiService: HttpClient
) {
    suspend inline fun <reified T : Responses> safeRequest(
        endpoint: String,
        type: RequestsType,
        body: Requests,
        crossinline onSuccess: suspend (T) -> ApiRequestResult
    ): ApiRequestResult {

        val urls = listOf(SERVER_URL, HOME_URL)
        var lastException: Exception? = null

        for (baseUrl in urls) {
            try {
                val fullUrl = baseUrl + endpoint
                return when (type) {
                    RequestsType.POST -> executeAndParsePost(fullUrl, body, onSuccess)
                    RequestsType.GET -> executeAndParseGet(fullUrl, onSuccess)
                }
            } catch (e: Exception) {
                lastException = e
                continue
            }
        }
        return ApiRequestResult.Error("All servers unreachable: ${lastException?.message}")
    }

    suspend inline fun <reified T : Responses> executeAndParsePost(
        url: String,
        body: Requests,
        crossinline onSuccess: suspend (T) -> ApiRequestResult
    ): ApiRequestResult {
        val response = apiService.post(url) {
            contentType(ContentType.Application.Json)
            setBody(body)
        }
        return handleResponse(response, onSuccess)
    }

    suspend inline fun <reified T : Responses> executeAndParseGet(
        url: String,
        crossinline onSuccess: suspend (T) -> ApiRequestResult
    ): ApiRequestResult {
        val response = apiService.get(url)
        return handleResponse(response, onSuccess)
    }

    suspend inline fun <reified T : Responses> handleResponse(
        response: HttpResponse,
        crossinline onSuccess: suspend (T) -> ApiRequestResult
    ): ApiRequestResult {
        return if (response.status.isSuccess()) {
            onSuccess(response.body<T>())
        } else {
            ApiRequestResult.Error("Server error: ${response.status.value}")
        }
    }

}