package eu.barcode_1d_improve.data

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import androidx.core.graphics.createBitmap

object LabeledBarcodeImage {

	fun compose(barcode: Bitmap, label: String): Bitmap {
		val padding = 40
		val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
			color = Color.BLACK
			typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
			textSize = 56f
		}

		// Shrink the text until it fits inside the barcode's width
		val maxWidth = barcode.width - 2 * padding
		while (paint.measureText(label) > maxWidth && paint.textSize > 12f) {
			paint.textSize -= 2f
		}
		val fm = paint.fontMetrics
		val textHeight = fm.descent - fm.ascent
		val headerHeight = (padding + textHeight + padding / 2f).toInt()

		val result = createBitmap(barcode.width, headerHeight + barcode.height)
		Canvas(result).apply {
			drawColor(Color.WHITE)
			drawText(label, (barcode.width - paint.measureText(label)) / 2f, padding - fm.ascent, paint)
			drawBitmap(barcode, 0f, headerHeight.toFloat(), null)
		}
		return result
	}
}