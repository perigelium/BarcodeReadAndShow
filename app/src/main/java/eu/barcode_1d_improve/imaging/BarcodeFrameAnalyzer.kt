package eu.barcode_1d_improve.imaging

import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import eu.barcode_1d_improve.data.BarcodeDetector
import eu.barcode_1d_improve.ui.restoration.FrameResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class BarcodeFrameAnalyzer(
	private val detector: BarcodeDetector,
	private val scope: CoroutineScope,
	private val onResult: (FrameResult) -> Unit,
                          ) : ImageAnalysis.Analyzer {

	@androidx.annotation.OptIn(ExperimentalGetImage::class)
	override fun analyze(imageProxy: ImageProxy) {
		val mediaImage = imageProxy.image
		if (mediaImage == null) {
			imageProxy.close()
			return
		}
		val rotation = imageProxy.imageInfo.rotationDegrees
		val input = InputImage.fromMediaImage(mediaImage, rotation)

		// ML Kit returns coordinates in the upright (rotated) frame
		val upright = rotation == 90 || rotation == 270
		val w = if (upright) imageProxy.height else imageProxy.width
		val h = if (upright) imageProxy.width else imageProxy.height

		scope.launch {
			try {
				detector.detect(input).onSuccess { onResult(FrameResult(w, h, it)) }
			} finally {
				imageProxy.close() // CameraX only delivers the next frame after this
			}
		}
	}
}