package apc.appcradle.kotlinjc_friendsactivity_app.domain

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import kotlinx.serialization.Serializable

class NetworkClient {

    companion object {
        @Serializable
        data class RegisterReceiveRemote(
            val login: String,
            val password: String
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
    }
    private val serverUrl = "http://212.3.131.67:5555/"

    suspend fun sendRegistrationInfo(
        login: String,
        password: String
    ): Boolean {
        val body = RegisterReceiveRemote(login = login, password = password)

        return try {
            val request = networkService.post(urlString = "$serverUrl/register") {
                contentType(ContentType.Application.Json)
                setBody(body)
            }
            request.status.isSuccess()
        } catch (e: Exception) {
            Log.e("dataTransfer", "not successful sending", e)
            false
        }
    }
}