package apc.appcradle.kotlinjc_friendsactivity_app.domain.model

import androidx.compose.runtime.Stable

@Stable
data class AuthData(
    val isLoggedIn: Boolean = false,
    val userLogin: String? = null
)
