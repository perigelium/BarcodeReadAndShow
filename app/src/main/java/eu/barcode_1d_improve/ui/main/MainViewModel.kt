package eu.barcode_1d_improve.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eu.barcode_1d_improve.delegation.MVI
import eu.barcode_1d_improve.delegation.mvi
import eu.barcode_1d_improve.ui.main.MainContract.UiAction
import eu.barcode_1d_improve.ui.main.MainContract.UiEffect
import eu.barcode_1d_improve.ui.main.MainContract.UiState
import kotlinx.coroutines.launch

class MainViewModel : ViewModel(), MVI<UiState, UiAction, UiEffect> by mvi(UiState()) {

	override fun onAction(uiAction: UiAction) {
		viewModelScope.launch {}
	}
}