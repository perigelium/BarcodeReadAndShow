package eu.barcode_1d_improve.imaging

import android.graphics.Bitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.opencv.android.Utils
import org.opencv.core.Mat
import androidx.core.graphics.createBitmap

class ProcessingPipeline {

	suspend fun run(source: Bitmap, steps: List<ProcessingStep>): List<StageResult> =
		withContext(Dispatchers.Default) {
			val results = mutableListOf<StageResult>()
			var current = Mat().also { Utils.bitmapToMat(source, it) }
			try {
				for (step in steps) {
					val start = System.nanoTime()
					val next = step.apply(current)
					val elapsedMs = (System.nanoTime() - start) / 1_000_000
					current.release()   // OpenCV Mats live in native memory
					current = next
					results += StageResult(step.name, current.toBitmap(), elapsedMs)
				}
			} finally {
				current.release()
			}
			results
		}

	private fun Mat.toBitmap(): Bitmap = createBitmap(cols(), rows())
			.also { Utils.matToBitmap(this, it) }
}