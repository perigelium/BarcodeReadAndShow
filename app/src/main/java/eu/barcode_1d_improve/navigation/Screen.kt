package eu.barcode_1d_improve.navigation

import kotlinx.serialization.Serializable

sealed interface Screen {
	@Serializable
	data object Main : Screen
}