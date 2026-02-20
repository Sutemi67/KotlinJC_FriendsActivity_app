package apc.appcradle.kotlinjc_friendsactivity_app.features.auth.model

import apc.appcradle.kotlinjc_friendsactivity_app.core.models.BaseState
import apc.appcradle.kotlinjc_friendsactivity_app.network.model.DataTransferState

data class AuthState(
    val isLoading: Boolean = false,
    val dataTransferState: DataTransferState = DataTransferState()
) : BaseState