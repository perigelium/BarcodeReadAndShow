package eu.barcode_1d_improve.data

import android.graphics.PointF

data class DetectedBarcode(
	val value: String?,
	val format: Symbology,
	val corners: List<PointF>,
                          )