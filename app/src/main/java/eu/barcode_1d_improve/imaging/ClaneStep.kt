package eu.barcode_1d_improve.imaging

import org.opencv.core.Mat
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc

class ClaheStep(
	private val clipLimit: Double = 2.0,  // 2-8
	private val tileSize: Int = 8,
               ) : ProcessingStep {

	override val name = "CLAHE"

	override fun apply(input: Mat): Mat {
		require(input.channels() == 1) { "CLAHE needs a grayscale input" }
		val out = Mat()
		val clahe = Imgproc.createCLAHE(clipLimit, Size(tileSize.toDouble(), tileSize.toDouble()))
		clahe.apply(input, out)
		return out
	}
}