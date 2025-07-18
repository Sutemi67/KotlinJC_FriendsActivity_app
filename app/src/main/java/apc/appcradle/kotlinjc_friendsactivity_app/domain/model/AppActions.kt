package apc.appcradle.kotlinjc_friendsactivity_app.domain.model

sealed interface AppActions {
    data class ChangeState(val state: Boolean) : AppActions
}