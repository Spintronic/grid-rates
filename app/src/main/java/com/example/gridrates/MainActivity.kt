package com.example.gridrates

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gridrates.ui.RootViewModel
import com.example.gridrates.ui.navigation.GridRatesNavigation
import com.example.gridrates.ui.theme.GridRatesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val application = application as GridRatesApplication
        setContent {
            GridRatesTheme(darkTheme = true) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainContent(application)
                }
            }
        }
    }
}

@Composable
fun MainContent(application: GridRatesApplication) {
    val rootViewModel: RootViewModel = viewModel(
        factory = RootViewModel.Factory(application.repository)
    )
    val startDestination by rootViewModel.startDestination.collectAsStateWithLifecycle()

    if (startDestination == null) {
        SplashScreen()
    } else {
        GridRatesNavigation(
            startRoute = startDestination!!,
            application = application
        )
    }
}

@Composable
fun SplashScreen() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = "GridRates",
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
