package eu.barcode_1d_improve.data

import android.graphics.Bitmap

data class GeneratedBarcode(
	val bitmap: Bitmap,
	val value: String,
	val symbology: Symbology,
	val roundTripOk: Boolean,
                           )