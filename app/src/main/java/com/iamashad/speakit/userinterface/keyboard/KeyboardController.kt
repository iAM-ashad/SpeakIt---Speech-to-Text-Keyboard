package com.iamashad.speakit.userinterface.keyboard


import com.iamashad.speakit.domain.VoiceInputCoordinator
import dagger.hilt.android.scopes.ServiceScoped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


sealed interface KeyboardUiState {
    data object Idle : KeyboardUiState
    data object Recording : KeyboardUiState
    data object Processing : KeyboardUiState
    data class Error(val message: String) : KeyboardUiState
    data class Inserted(val text: String) : KeyboardUiState
}


sealed interface KeyboardUiEvent {
    data object PressDown : KeyboardUiEvent
    data object Release : KeyboardUiEvent
    data object Cancel : KeyboardUiEvent
}

@ServiceScoped
class KeyboardController @Inject constructor(
    private val coordinator: VoiceInputCoordinator
) {
    private val scope = CoroutineScope(Dispatchers.Main.immediate)

    private val _state = MutableStateFlow<KeyboardUiState>(KeyboardUiState.Idle)
    val state: StateFlow<KeyboardUiState> = _state

    private var inflight: Job? = null
    private var lastInserted = 0

    fun onPressDown() {
        if (_state.value is KeyboardUiState.Processing) return
        _state.value = KeyboardUiState.Recording
        inflight?.cancel()
        inflight = scope.launch {
            coordinator.onPressDownStartRecording()
                .onFailure { e ->
                    _state.value = KeyboardUiState.Error(e.message ?: "Failed to start")
                }
        }
    }

    fun onRelease(onTranscribed: (String) -> Unit) {
        if (_state.value !is KeyboardUiState.Recording) return
        _state.value = KeyboardUiState.Processing
        inflight?.cancel()
        inflight = scope.launch {
            coordinator.onReleaseStopAndTranscribe().onSuccess { text ->
                lastInserted = text.length
                _state.value = KeyboardUiState.Inserted(text)
                onTranscribed(text)
                delay(1200)
                _state.value = KeyboardUiState.Idle
            }.onFailure { e ->
                _state.value = KeyboardUiState.Error(e.message ?: "Transcription failed")
            }
        }
    }

    fun onCancel() {
        coordinator.cancel()
        _state.value = KeyboardUiState.Idle
        inflight?.cancel()
    }
}