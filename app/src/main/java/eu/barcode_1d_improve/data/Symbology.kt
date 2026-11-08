package eu.barcode_1d_improve.data

import com.google.zxing.BarcodeFormat

enum class Symbology(val label: String, val zxing: BarcodeFormat?) {
	EAN_13("EAN-13", BarcodeFormat.EAN_13),
	EAN_8("EAN-8", BarcodeFormat.EAN_8),
	UPC_A("UPC-A", BarcodeFormat.UPC_A),
	UPC_E("UPC-E", BarcodeFormat.UPC_E),
	CODE_128("Code 128", BarcodeFormat.CODE_128),
	CODE_39("Code 39", BarcodeFormat.CODE_39),
	CODE_93("Code 93", BarcodeFormat.CODE_93),
	CODABAR("Codabar", BarcodeFormat.CODABAR),
	ITF("ITF", BarcodeFormat.ITF),
	UNKNOWN("Unknown", null),
}