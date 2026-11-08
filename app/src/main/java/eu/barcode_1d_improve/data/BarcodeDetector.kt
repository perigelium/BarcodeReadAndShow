package eu.barcode_1d_improve.data

import android.graphics.Bitmap
import android.graphics.PointF
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class BarcodeDetector {

	private val scanner = BarcodeScanning.getClient(
		BarcodeScannerOptions.Builder()
			.setBarcodeFormats(
				Barcode.FORMAT_EAN_13,
				Barcode.FORMAT_EAN_8,
				Barcode.FORMAT_UPC_A,
				Barcode.FORMAT_UPC_E,
				Barcode.FORMAT_CODE_128,
				Barcode.FORMAT_CODE_39,
				Barcode.FORMAT_CODE_93,
				Barcode.FORMAT_CODABAR,
				Barcode.FORMAT_ITF,
			                  )
			.build()
	                                               )


	suspend fun detect(bitmap: Bitmap): Result<List<DetectedBarcode>> =
		detect(InputImage.fromBitmap(bitmap, 0))

	suspend fun detect(image: InputImage): Result<List<DetectedBarcode>> = runCatching {
		scanner.process(image).awaitResult().map { it.toDetected() }
	}

	private fun Barcode.toDetected() = DetectedBarcode(
		value = rawValue,
		format = symbologyOf(format),
		corners = cornerPoints.orEmpty().map { PointF(it.x.toFloat(), it.y.toFloat()) },
	                                                  )

	private fun symbologyOf(format: Int) = when (format) {
		Barcode.FORMAT_EAN_13 -> Symbology.EAN_13
		Barcode.FORMAT_EAN_8 -> Symbology.EAN_8
		Barcode.FORMAT_UPC_A -> Symbology.UPC_A
		Barcode.FORMAT_UPC_E -> Symbology.UPC_E
		Barcode.FORMAT_CODE_128 -> Symbology.CODE_128
		Barcode.FORMAT_CODE_39 -> Symbology.CODE_39
		Barcode.FORMAT_CODE_93 -> Symbology.CODE_93
		Barcode.FORMAT_CODABAR -> Symbology.CODABAR
		Barcode.FORMAT_ITF -> Symbology.ITF
		else -> Symbology.UNKNOWN
	}

}

private suspend fun <T> Task<T>.awaitResult(): T = suspendCancellableCoroutine { cont ->
	addOnSuccessListener { cont.resume(it) }
	addOnFailureListener { cont.resumeWithException(it) }
}