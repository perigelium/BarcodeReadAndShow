package eu.barcode_1d_improve.imaging

import eu.barcode_1d_improve.data.BarcodeDetector
import eu.barcode_1d_improve.data.BarcodeGenerator
import eu.barcode_1d_improve.data.GeneratedBarcode
import eu.barcode_1d_improve.data.Symbology

class RenderBarcode(
	private val generator: BarcodeGenerator,
	private val detector: BarcodeDetector,
                   ) {

	suspend operator fun invoke(
		value: String,
		symbology: Symbology,
		widthPx: Int = 1200,
		heightPx: Int = 400,
	                           ): Result<GeneratedBarcode> =
		generator.generate(value, symbology, widthPx, heightPx).mapCatching { bitmap ->
			val ok = detector.detect(bitmap).getOrNull()
				?.any { it.value == value && it.format == symbology } == true
			check(ok) { "Generated barcode failed the round-trip check" }
			GeneratedBarcode(bitmap, value, symbology, roundTripOk = true)
		}
}