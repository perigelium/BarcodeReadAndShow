package eu.barcode_1d_improve.ui.restoration

import android.graphics.Bitmap
import android.net.Uri
import eu.barcode_1d_improve.data.DetectedBarcode
import eu.barcode_1d_improve.data.GeneratedBarcode
import eu.barcode_1d_improve.delegation.UiEffect
import eu.barcode_1d_improve.delegation.UiAction
import eu.barcode_1d_improve.delegation.UiState
import eu.barcode_1d_improve.imaging.StageResult

data class RestorationState(
	val original: Bitmap? = null,
	val isLoading: Boolean = false,
	val isDetecting: Boolean = false,
	val detections: Map<Int, List<DetectedBarcode>> = emptyMap(),
	val generated: GeneratedBarcode? = null,
	val fullScreen: GeneratedBarcode? = null,
	val isProcessing: Boolean = false,
	val isSaving: Boolean = false,
	val stages: List<StageResult> = emptyList(),
	val selectedStageIndex: Int = 0, // 0 = original, i = stages[i - 1]
                           ) : UiState

sealed interface RestorationAction : UiAction {
	data class ImageSelected(val uri: Uri) : RestorationAction
	data object DetectClicked : RestorationAction
	data object GenerateClicked : RestorationAction
	data object RunPipelineClicked : RestorationAction
	data class ShowFullScreenClicked(val widthPx: Int, val heightPx: Int) : RestorationAction
	data object FullScreenDismissed : RestorationAction
	data class StageSelected(val index: Int) : RestorationAction
	data object SaveGeneratedClicked : RestorationAction
}

sealed interface RestorationEffect : UiEffect {
	data class ShowMessage(val text: String) : RestorationEffect
}