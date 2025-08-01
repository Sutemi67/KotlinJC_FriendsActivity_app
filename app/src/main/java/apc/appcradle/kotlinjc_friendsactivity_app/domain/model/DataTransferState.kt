package apc.appcradle.kotlinjc_friendsactivity_app.domain.model

data class DataTransferState(
    val isLoading: Boolean = false,
    val isSuccessful: Boolean? = null,
    val errorMessage: String? = null
)
