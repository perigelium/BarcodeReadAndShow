package eu.barcode_1d_improve.imaging

import org.opencv.core.Mat
import org.opencv.imgproc.Imgproc

class GrayscaleStep : ProcessingStep {
	override val name = "Grayscale"

	override fun apply(input: Mat): Mat {
		val out = Mat()
		Imgproc.cvtColor(input, out, Imgproc.COLOR_RGBA2GRAY)
		return out
	}
}