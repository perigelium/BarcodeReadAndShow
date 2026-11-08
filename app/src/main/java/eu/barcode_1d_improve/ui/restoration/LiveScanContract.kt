package eu.barcode_1d_improve.ui.restoration

import eu.barcode_1d_improve.data.DetectedBarcode
import eu.barcode_1d_improve.data.GeneratedBarcode
import eu.barcode_1d_improve.data.Symbology
import eu.barcode_1d_improve.delegation.UiAction
import eu.barcode_1d_improve.delegation.UiEffect
import eu.barcode_1d_improve.delegation.UiState

data class ScanRead(val value: String, val symbology: Symbology)

data class VerifiedRead(val read: ScanRead, val votes: Int, val framesInWindow: Int)

object LiveScanConfig {
	const val WINDOW = 12   // recent frames with a read that are considered
	const val REQUIRED = 8  // how many of them must agree
}

data class FrameResult(val width: Int, val height: Int, val barcodes: List<DetectedBarcode>)

data class LiveScanState(
	val frame: FrameResult? = null,
	val seen: Map<String, Int> = emptyMap(),       // raw counters, kept for debugging
	val window: List<Set<ScanRead>> = emptyList(),
	val bestVotes: Int = 0,
	val verified: VerifiedRead? = null,
	val isSaving: Boolean = false,
	val fullScreen: GeneratedBarcode? = null,
                        ) : UiState

sealed interface LiveScanAction : UiAction {
	data class FrameAnalyzed(val result: FrameResult) : LiveScanAction
	data object Reset : LiveScanAction
	data class ShowFullScreenClicked(val widthPx: Int, val heightPx: Int) : LiveScanAction
	data object FullScreenDismissed : LiveScanAction
	data object SaveClicked : LiveScanAction
}

sealed interface LiveScanEffect : UiEffect {
	data class ShowMessage(val text: String) : LiveScanEffect
}