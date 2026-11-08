package eu.barcode_1d_improve.delegation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

interface UiState
interface UiAction
interface UiEffect

abstract class MviViewModel<S : UiState, I : UiAction, E : UiEffect>(
	initialState: S,
                                                                    ) : ViewModel() {

	private val _state = MutableStateFlow(initialState)
	val state: StateFlow<S> = _state.asStateFlow()

	private val _effects = Channel<E>(Channel.BUFFERED)
	val effects: Flow<E> = _effects.receiveAsFlow()

	fun onAction(action: I) {
		viewModelScope.launch { handle(action) }
	}

	protected abstract suspend fun handle(action: I)

	protected fun updateState(transform: S.() -> S) {
		_state.update { it.transform() }
	}

	protected fun emit(effect: E) {
		_effects.trySend(effect)
	}
}