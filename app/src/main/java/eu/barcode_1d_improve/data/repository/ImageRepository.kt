package eu.barcode_1d_improve.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt

class ImageRepository(private val context: Context) {

	suspend fun load(uri: Uri): Result<Bitmap> = withContext(Dispatchers.IO) {
		runCatching {
			val source = ImageDecoder.createSource(context.contentResolver, uri)
			ImageDecoder.decodeBitmap(source) { decoder, info, _ ->
				// Software bitmap: OpenCV and ML Kit cannot read hardware bitmaps
				decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
				val maxSide = maxOf(info.size.width, info.size.height)
				if (maxSide > MAX_SIDE) {
					val scale = MAX_SIDE.toFloat() / maxSide
					decoder.setTargetSize(
						(info.size.width * scale).roundToInt(),
						(info.size.height * scale).roundToInt(),
					                     )
				}
			}
		}
	}

	private companion object {
		const val MAX_SIDE = 4096
	}
}