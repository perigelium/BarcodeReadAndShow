package eu.barcode_1d_improve.ui.main

import androidx.compose.ui.tooling.preview.PreviewParameterProvider

class MainScreenPreviewProvider : PreviewParameterProvider<MainContract.UiState> {
	override val values: Sequence<MainContract.UiState>
		get() = sequenceOf(
			MainContract.UiState(
				isLoading = true,
				list = emptyList(),
			                    ),
			MainContract.UiState(
				isLoading = false,
				list = emptyList(),
			                    ),
			MainContract.UiState(
				isLoading = false, list = listOf("Item 1", "Item 2", "Item 3")
			                    ),
		                  )
}