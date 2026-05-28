package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.db.AppDatabase
import com.example.data.db.PlantRepository
import com.example.ui.PlantViewModel
import com.example.ui.PlantViewModelFactory
import com.example.ui.screens.HomeScreen
import com.example.ui.theme.MyLittlePlantTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Let the Android window stretch behind status/navigation notches
        enableEdgeToEdge()

        // Local SQLite Room database setup
        val database = AppDatabase.getDatabase(applicationContext)
        val repository = PlantRepository(database.plantDao())

        // Connect MVVM layer using our customized factory
        val viewModel: PlantViewModel by viewModels {
            PlantViewModelFactory(repository)
        }

        setContent {
            val isGreenTheme by viewModel.isGreenTheme.collectAsStateWithLifecycle()

            // Apply our custom theme wrapper holding BOTH requested gorgeous color palettes
            MyLittlePlantTheme(isGreenTheme = isGreenTheme, darkTheme = false) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = androidx.compose.material3.MaterialTheme.colorScheme.background
                ) {
                    HomeScreen(viewModel = viewModel)
                }
            }
        }
    }
}
