package apc.appcradle.kotlinjc_friendsactivity_app.features.auth.model

import apc.appcradle.kotlinjc_friendsactivity_app.core.models.BaseEvents

interface AuthEvents : BaseEvents {
    data class Login(val login: String, val password: String) : AuthEvents
    data class Registration(val login: String, val password: String) : AuthEvents
    object GoOffline : AuthEvents
}