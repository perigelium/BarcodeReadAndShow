package eu.barcode_1d_improve.ui.restoration

import android.net.Uri
import eu.barcode_1d_improve.data.BarcodeDetector
import eu.barcode_1d_improve.data.BarcodeGenerator
import eu.barcode_1d_improve.data.GeneratedBarcode
import eu.barcode_1d_improve.data.ImageExporter
import eu.barcode_1d_improve.data.repository.ImageRepository
import eu.barcode_1d_improve.delegation.MviViewModel
import eu.barcode_1d_improve.imaging.ClaheStep
import eu.barcode_1d_improve.imaging.GrayscaleStep
import eu.barcode_1d_improve.imaging.ProcessingPipeline

class RestorationViewModel(
	private val imageRepository: ImageRepository,
	private val detector: BarcodeDetector,
	private val pipeline: ProcessingPipeline,
	private val barcodeGenerator: BarcodeGenerator,
	private val imageExporter: ImageExporter
                          ) :
	MviViewModel<RestorationState, RestorationAction, RestorationEffect>(RestorationState()) {

	override suspend fun handle(action: RestorationAction) {
		when (action) {
			is RestorationAction.ImageSelected -> loadImage(action.uri)
			RestorationAction.DetectClicked -> detectShown()
			RestorationAction.RunPipelineClicked -> runPipeline()
			is RestorationAction.StageSelected -> updateState { copy(selectedStageIndex = action.index) }
			RestorationAction.GenerateClicked -> generateBarcode()
			is RestorationAction.ShowFullScreenClicked -> showFullScreen(
				action.widthPx,
				action.heightPx
			                                                            )

			RestorationAction.FullScreenDismissed -> updateState { copy(fullScreen = null) }
			RestorationAction.SaveGeneratedClicked -> saveGenerated()
		}
	}

	private suspend fun generateBarcode() {
		val s = state.value
		val barcode =
			s.detections[s.selectedStageIndex]?.firstOrNull { it.value != null && it.format.zxing != null }
		if (barcode == null) {
			emit(RestorationEffect.ShowMessage("No decoded barcode on the shown image"))
			return
		}
		val value = barcode.value!!
		barcodeGenerator.generate(value, barcode.format).onSuccess { bitmap ->
				val roundTripOk = detector.detect(bitmap).getOrNull()
					?.any { it.value == value && it.format == barcode.format } == true
				updateState {
					copy(
						generated = GeneratedBarcode(
							bitmap, value, barcode.format, roundTripOk
						                            )
					    )
				}
			}.onFailure { emit(RestorationEffect.ShowMessage("Cannot generate: ${it.message}")) }
	}

	private suspend fun showFullScreen(widthPx: Int, heightPx: Int) {
		val g = state.value.generated ?: return
		barcodeGenerator.generate(g.value, g.symbology, widthPx, heightPx).onSuccess { bitmap ->
				val ok = detector.detect(bitmap).getOrNull()
					?.any { it.value == g.value && it.format == g.symbology } == true
				if (ok) {
					updateState { copy(fullScreen = g.copy(bitmap = bitmap, roundTripOk = true)) }
				} else {
					emit(RestorationEffect.ShowMessage("Full-screen barcode failed the round-trip check"))
				}
			}.onFailure { emit(RestorationEffect.ShowMessage("Cannot generate: ${it.message}")) }
	}

	private suspend fun detectShown() {
		val s = state.value
		val index = s.selectedStageIndex
		val bitmap = s.stages.getOrNull(index - 1)?.image ?: s.original ?: return
		updateState { copy(isDetecting = true) }
		detector.detect(bitmap).onSuccess { found ->
				updateState {
					copy(
						detections = detections + (index to found),
						isDetecting = false
					    )
				}
			}.onFailure { e ->
				updateState { copy(isDetecting = false) }
				emit(RestorationEffect.ShowMessage("Detection failed: ${e.message}"))
			}
	}

	private suspend fun runPipeline() {
		val bitmap = state.value.original ?: return
		updateState { copy(isProcessing = true) }
		runCatching {
			pipeline.run(bitmap, listOf(GrayscaleStep(), ClaheStep()))
		}.onSuccess { stages ->
				updateState {
					copy(
						stages = stages,
						selectedStageIndex = 0,
						isProcessing = false,
						detections = detections.filterKeys { it == 0 })
				}
			}.onFailure { e ->
				updateState { copy(isProcessing = false) }
				emit(RestorationEffect.ShowMessage("Pipeline failed: ${e.message}"))
			}
	}

	private suspend fun loadImage(uri: Uri) {
		imageRepository.load(uri).onSuccess { bitmap ->
				updateState {
					copy(
						generated = null,
						fullScreen = null,
						original = bitmap,
						isLoading = false,
						detections = emptyMap(),
						stages = emptyList(),
						selectedStageIndex = 0
					    )
				}
			}.onFailure { e ->
				updateState { copy(isLoading = false) }
				emit(RestorationEffect.ShowMessage("Failed to load image: ${e.message}"))
			}
	}

	private suspend fun saveGenerated() {
		val g = state.value.generated ?: return
		updateState { copy(isSaving = true) }
		imageExporter.saveBarcode(g)
			.onSuccess { emit(RestorationEffect.ShowMessage("Saved to Pictures/Barcode1D")) }
			.onFailure { emit(RestorationEffect.ShowMessage("Save failed: ${it.message}")) }
		updateState { copy(isSaving = false) }
	}
}