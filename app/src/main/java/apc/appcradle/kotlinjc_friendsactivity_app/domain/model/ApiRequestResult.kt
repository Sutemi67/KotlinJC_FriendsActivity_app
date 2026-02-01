package apc.appcradle.kotlinjc_friendsactivity_app.domain.model

import apc.appcradle.kotlinjc_friendsactivity_app.domain.model.network.DataTransferState

sealed interface ApiRequestResult {
    data class Error(val message: String?) : ApiRequestResult
    data class Success(val result: DataTransferState) : ApiRequestResult
}