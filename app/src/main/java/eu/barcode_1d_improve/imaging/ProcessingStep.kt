package eu.barcode_1d_improve.imaging

import android.graphics.Bitmap
import org.opencv.core.Mat

interface ProcessingStep {
	val name: String

	/** Must return a NEW Mat. Never return or modify the input. */
	fun apply(input: Mat): Mat
}

data class StageResult(
	val name: String,
	val image: Bitmap,
	val durationMs: Long,
                      )