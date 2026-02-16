package apc.appcradle.kotlinjc_friendsactivity_app.core.models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import apc.appcradle.kotlinjc_friendsactivity_app.core.utils.LoggerType
import apc.appcradle.kotlinjc_friendsactivity_app.core.utils.logger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

abstract class BaseViewModel<State : BaseState, Event : BaseEvents, Action : BaseActions>(
    initialState: State
) : ViewModel() {
    protected val mutableState = MutableStateFlow(initialState)
    val state = mutableState.asStateFlow()

    protected val innerAction = MutableStateFlow<Action?>(null)
    val action = innerAction.asStateFlow()

    abstract fun obtainEvent(event: Event)

    protected fun runSafely(
        block: suspend () -> Unit,
        onFailure: (suspend (Throwable) -> Unit)? = null
    ) {
        viewModelScope.launch {
            runCatching {
                block()
            }.onFailure { error ->
                logger(LoggerType.Error, error.message ?: "error is $error")
                onFailure?.invoke(error)
            }
        }
    }
}