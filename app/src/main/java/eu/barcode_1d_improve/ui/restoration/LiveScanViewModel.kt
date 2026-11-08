package eu.barcode_1d_improve.ui.restoration

import eu.barcode_1d_improve.data.ImageExporter
import eu.barcode_1d_improve.delegation.MviViewModel
import eu.barcode_1d_improve.imaging.RenderBarcode

class LiveScanViewModel(private val renderBarcode: RenderBarcode, private val imageExporter: ImageExporter) :
	MviViewModel<LiveScanState, LiveScanAction, LiveScanEffect>(LiveScanState()) {

	override suspend fun handle(action: LiveScanAction) {
		when (action) {
			is LiveScanAction.FrameAnalyzed -> updateState {
				if (verified != null) return@updateState this   // locked until Reset

				val reads = action.result.barcodes
					.mapNotNull { b -> b.value?.let { ScanRead(it, b.format) } }
					.toSet()

				val counts = seen.toMutableMap()
				reads.forEach { counts[it.value] = (counts[it.value] ?: 0) + 1 }

				val newWindow = if (reads.isEmpty()) window
				else (window + listOf(reads)).takeLast(LiveScanConfig.WINDOW)

				val best = newWindow.flatten().groupingBy { it }.eachCount().maxByOrNull { it.value }
				val votes = best?.value ?: 0

				copy(
					frame = action.result,
					seen = counts,
					window = newWindow,
					bestVotes = votes,
					verified = best
						?.takeIf { votes >= LiveScanConfig.REQUIRED }
						?.let { VerifiedRead(it.key, votes, newWindow.size) },
				    )
			}

			is LiveScanAction.ShowFullScreenClicked -> showFullScreen(action.widthPx, action.heightPx)
			LiveScanAction.FullScreenDismissed -> updateState { copy(fullScreen = null) }
			LiveScanAction.SaveClicked -> save()
			LiveScanAction.Reset -> updateState { LiveScanState() }
		}
	}

	private suspend fun showFullScreen(widthPx: Int, heightPx: Int) {
		val read = state.value.verified?.read ?: return
		renderBarcode(read.value, read.symbology, widthPx, heightPx)
			.onSuccess { barcode -> updateState { copy(fullScreen = barcode) } }
			.onFailure { emit(LiveScanEffect.ShowMessage("Cannot show barcode: ${it.message}")) }
	}

	private suspend fun save() {
		val read = state.value.verified?.read ?: return
		updateState { copy(isSaving = true) }
		renderBarcode(read.value, read.symbology)
			.mapCatching { imageExporter.saveBarcode(it).getOrThrow() }
			.onSuccess { emit(LiveScanEffect.ShowMessage("Saved to Pictures/Barcode1D")) }
			.onFailure { emit(LiveScanEffect.ShowMessage("Save failed: ${it.message}")) }
		updateState { copy(isSaving = false) }
	}
}