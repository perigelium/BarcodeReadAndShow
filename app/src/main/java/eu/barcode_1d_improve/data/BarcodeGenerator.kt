package eu.barcode_1d_improve.data

import android.graphics.Bitmap
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class BarcodeGenerator {

	suspend fun generate(
		value: String,
		symbology: Symbology,
		width: Int = 1200,
		height: Int = 400,
	                    ): Result<Bitmap> = withContext(Dispatchers.Default) {
		runCatching {
			val format: BarcodeFormat = requireNotNull(symbology.zxing) { "Unsupported format: ${symbology.label}" }
			val matrix = MultiFormatWriter().encode(value, format, width, height)
			val pixels = IntArray(matrix.width * matrix.height) { i ->
				if (matrix[i % matrix.width, i / matrix.width]) Color.Black.toArgb() else Color.White.toArgb()
			}
			Bitmap.createBitmap(pixels, matrix.width, matrix.height, Bitmap.Config.ARGB_8888)
		}
	}
}