package eu.barcode_1d_improve.di

import eu.barcode_1d_improve.data.BarcodeDetector
import eu.barcode_1d_improve.data.BarcodeGenerator
import eu.barcode_1d_improve.data.ImageExporter
import eu.barcode_1d_improve.data.repository.ImageRepository
import eu.barcode_1d_improve.imaging.ProcessingPipeline
import eu.barcode_1d_improve.imaging.RenderBarcode
import eu.barcode_1d_improve.ui.main.MainViewModel
import eu.barcode_1d_improve.ui.restoration.LiveScanViewModel
import eu.barcode_1d_improve.ui.restoration.RestorationViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val restorationModule = module {
	single { ProcessingPipeline() }
	single { ImageRepository(androidContext()) }
	single { BarcodeDetector() }
	single { BarcodeGenerator() }
	single { RenderBarcode(get(), get()) }
	single { ImageExporter(androidContext()) }
	viewModelOf(::RestorationViewModel)
	viewModelOf(::LiveScanViewModel)
}