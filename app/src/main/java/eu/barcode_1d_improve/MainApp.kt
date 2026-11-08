package eu.barcode_1d_improve

import android.app.Application
import android.util.Log
import eu.barcode_1d_improve.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.opencv.android.OpenCVLoader

class MainApp : Application() {
	override fun onCreate() {
		super.onCreate()
		startKoin {
			androidContext(this@MainApp)
			modules(appModule)
		}
		Log.d("App", "OpenCV loaded: ${OpenCVLoader.initLocal()}")
	}
}