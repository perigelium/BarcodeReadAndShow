package eu.barcode_1d_improve.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import eu.barcode_1d_improve.navigation.NavigationGraph
import eu.barcode_1d_improve.navigation.Screen.Main
import eu.barcode_1d_improve.ui.restoration.LiveScanScreen
import eu.barcode_1d_improve.ui.restoration.RestorationScreen
import eu.barcode_1d_improve.ui.theme.MyappTheme

class MainActivity : ComponentActivity() {

	enum class AppScreen { Restoration, Live }

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		enableEdgeToEdge()

		setContent {
			MyappTheme {/* 				Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
					val navController = rememberNavController()
					val startDestination = Main
					NavigationGraph(
						navController = navController,
						startDestination = startDestination,
						modifier = Modifier.padding(innerPadding)
					               )
				} */


				//RestorationScreen()

				var screen by rememberSaveable { mutableStateOf(AppScreen.Restoration) }
				when (screen) {
					AppScreen.Restoration -> RestorationScreen(onOpenLive = {
						screen = AppScreen.Live
					})

					AppScreen.Live -> LiveScanScreen(onClose = { screen = AppScreen.Restoration })
				}
			}
		}

	}
}



