package eu.barcode_1d_improve.ui.main

object MainContract {
	data class UiState(
		val isLoading: Boolean = false,
		val list: List<String> = emptyList(),
	                  )

	sealed interface UiAction

	sealed interface UiEffect
}