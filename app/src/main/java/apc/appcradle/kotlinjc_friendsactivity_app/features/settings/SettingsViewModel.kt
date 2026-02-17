package apc.appcradle.kotlinjc_friendsactivity_app.features.settings

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Immutable
import apc.appcradle.kotlinjc_friendsactivity_app.core.models.BaseViewModel
import apc.appcradle.kotlinjc_friendsactivity_app.core.models.ISettingsRepository
import apc.appcradle.kotlinjc_friendsactivity_app.core.models.ITokenRepository
import apc.appcradle.kotlinjc_friendsactivity_app.core.services.StepCounterService
import apc.appcradle.kotlinjc_friendsactivity_app.core.utils.LoggerType
import apc.appcradle.kotlinjc_friendsactivity_app.core.utils.logger
import apc.appcradle.kotlinjc_friendsactivity_app.features.settings.model.SettingsActions
import apc.appcradle.kotlinjc_friendsactivity_app.features.settings.model.SettingsEvents
import apc.appcradle.kotlinjc_friendsactivity_app.features.settings.model.SettingsState
import apc.appcradle.kotlinjc_friendsactivity_app.network.NetworkClient
import kotlinx.coroutines.flow.update

@Immutable
class SettingsViewModel(
    private val settingsRepo: ISettingsRepository,
    private val tokenRepository: ITokenRepository,
    private val networkClient: NetworkClient,
) : BaseViewModel<SettingsState, SettingsEvents, SettingsActions>(initialState = settingsRepo.settingsState.value) {

    init {
        logger(LoggerType.Debug, "SettingsViewModel INIT: ${this.hashCode()}")
    }

    override fun obtainEvent(event: SettingsEvents) {
        when (event) {
            is SettingsEvents.ChangeScale -> {
                mutableState.update { it.copy(userScale = event.newScaleValue) }
                saveSettings()
            }

            is SettingsEvents.ChangeTheme -> {
                mutableState.update { it.copy(currentTheme = event.newTheme) }
                saveSettings()
            }

            is SettingsEvents.ChangeLogin -> {
                runSafely(block = {
                    val isChangeSuccessful =
                        networkClient.changeUserLogin(state.value.userLogin!!, event.newLogin)

                    if (isChangeSuccessful) {
                        tokenRepository.saveNewLogin(event.newLogin)
                        mutableState.update { it.copy(userLogin = event.newLogin) }
                        saveSettings()
                    }
                })
            }

            is SettingsEvents.Logout -> {
                runSafely(
                    block = { tokenRepository.clearToken() }
                )
            }

            is SettingsEvents.ChangeStepLength -> {
                mutableState.update { it.copy(userStepLength = event.newStepValue) }
                saveSettings()
            }

            is SettingsEvents.OnServiceCheckerClick -> {
                if (event.checkerValue) startService(event.context) else stopService(event.context)
            }

            is SettingsEvents.StartService -> startService(event.context)
            is SettingsEvents.StopService -> stopService(event.context)
            is SettingsEvents.SaveSettings -> saveSettings()
            is SettingsEvents.LoadSettings -> loadSettings()
        }
    }

    private fun saveSettings() = settingsRepo.saveSettingsData(state = state.value)
    private fun loadSettings() = mutableState.update { settingsRepo.loadSettingsData() }
    private fun startService(context: Context) {
        val serviceIntent = Intent(context, StepCounterService::class.java)
        try {
            context.startForegroundService(serviceIntent)
            mutableState.update { it.copy(isServiceRunning = true) }
            saveSettings()
        } catch (e: Exception) {
            logger(LoggerType.Error, "Failed to start service: ${e.message}")
        }
    }

    private fun stopService(context: Context) {
        val serviceIntent = Intent(context, StepCounterService::class.java)
        context.stopService(serviceIntent)
        mutableState.update { it.copy(isServiceRunning = false) }
        saveSettings()
    }
}