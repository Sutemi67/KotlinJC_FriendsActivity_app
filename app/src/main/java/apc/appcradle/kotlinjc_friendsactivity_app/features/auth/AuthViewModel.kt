package apc.appcradle.kotlinjc_friendsactivity_app.features.auth

import androidx.compose.runtime.Immutable
import androidx.lifecycle.viewModelScope
import apc.appcradle.kotlinjc_friendsactivity_app.core.models.BaseViewModel
import apc.appcradle.kotlinjc_friendsactivity_app.features.auth.model.AuthActions
import apc.appcradle.kotlinjc_friendsactivity_app.features.auth.model.AuthEvents
import apc.appcradle.kotlinjc_friendsactivity_app.features.auth.model.AuthState
import apc.appcradle.kotlinjc_friendsactivity_app.features.auth.model.ITokenRepository
import apc.appcradle.kotlinjc_friendsactivity_app.network.NetworkClient
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@Immutable
class AuthViewModel(
    private val networkClient: NetworkClient,
    private val tokenRepository: ITokenRepository,
) :
    BaseViewModel<AuthState, AuthEvents, AuthActions>(initialState = AuthState()) {

    override fun obtainEvent(event: AuthEvents) {
        when (event) {
            is AuthEvents.GoOffline -> offline()
            is AuthEvents.Login -> login(event)
            is AuthEvents.Registration -> register(event)
        }
    }

    private fun offline() {
        viewModelScope.launch { tokenRepository.saveOfflineToken() }
    }

    private fun login(event: AuthEvents.Login) {
        viewModelScope.launch {
            mutableState.update { it.copy(isLoading = true) }
            val result = networkClient.sendLoginInfo(event.login, event.password)
            mutableState.update {
                it.copy(
                    isLoading = false,
                    dataTransferState = result
                )
            }
        }
    }

    private fun register(event: AuthEvents.Registration) {
        viewModelScope.launch {
            mutableState.update { it.copy(isLoading = true) }
            val result = networkClient.sendRegistrationInfo(event.login, event.password)
            mutableState.update {
                it.copy(
                    isLoading = false,
                    dataTransferState = result
                )
            }
        }
    }
}