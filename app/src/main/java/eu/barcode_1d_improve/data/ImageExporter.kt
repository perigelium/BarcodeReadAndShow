package eu.barcode_1d_improve.data

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ImageExporter(private val context: Context) {

	suspend fun saveBarcode(barcode: GeneratedBarcode): Result<Uri> =
		saveToGallery(
			bitmap = LabeledBarcodeImage.compose(
				barcode.bitmap,
				"${barcode.symbology.label}: ${barcode.value}",
			                                    ),
			displayName = fileName(barcode),
		             )

	private fun fileName(b: GeneratedBarcode): String {
		val safeValue = b.value.replace(Regex("[^A-Za-z0-9]"), "_").take(40)
		return "barcode_${b.symbology.name}_$safeValue"
	}

	private suspend fun saveToGallery(bitmap: Bitmap, displayName: String): Result<Uri> =
		withContext(Dispatchers.IO) {
			runCatching {
				val resolver = context.contentResolver
				val values = ContentValues().apply {
					put(MediaStore.Images.Media.DISPLAY_NAME, "$displayName.png")
					put(MediaStore.Images.Media.MIME_TYPE, "image/png")
					put(
						MediaStore.Images.Media.RELATIVE_PATH,
						"${Environment.DIRECTORY_PICTURES}/Barcode1D",
					   )
					put(MediaStore.Images.Media.IS_PENDING, 1) // hidden until fully written
				}
				val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
					?: error("Cannot create gallery entry")
				try {
					val stream = resolver.openOutputStream(uri) ?: error("Cannot open output stream")
					stream.use {
						check(bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)) {
							"PNG compression failed"
						}
					}
					values.clear()
					values.put(MediaStore.Images.Media.IS_PENDING, 0)
					resolver.update(uri, values, null, null)
					uri
				} catch (e: Throwable) {
					resolver.delete(uri, null, null) // no half-written file left behind
					throw e
				}
			}
		}
}