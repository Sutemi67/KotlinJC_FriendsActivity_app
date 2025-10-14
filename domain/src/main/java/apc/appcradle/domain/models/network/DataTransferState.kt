package apc.appcradle.domain.models.network

data class DataTransferState(
    val isLoading: Boolean = false,
    val isSuccessful: Boolean? = null,
    val errorMessage: String? = null
)