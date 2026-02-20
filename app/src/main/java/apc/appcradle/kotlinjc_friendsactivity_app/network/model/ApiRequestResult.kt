package apc.appcradle.kotlinjc_friendsactivity_app.network.model

sealed interface ApiRequestResult {
    data class Error(val message: String?) : ApiRequestResult
    data class Success(val result: DataTransferState) : ApiRequestResult
}