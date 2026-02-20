package apc.appcradle.kotlinjc_friendsactivity_app.network.model

import androidx.compose.runtime.Immutable

@Immutable
data class DataTransferState(
    val isLoading: Boolean = false,
    val isSuccessful: Boolean? = null,
    val errorMessage: String? = null,
    val response: Responses? = null
)