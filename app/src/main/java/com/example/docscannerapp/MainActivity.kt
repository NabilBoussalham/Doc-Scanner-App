package com.example.docscannerapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.docscannerapp.ui.screens.home.HomeScreen
import com.example.docscannerapp.ui.theme.DocScannerAppTheme
import com.example.docscannerapp.ui.viewmodels.PdfViewModel

class MainActivity : ComponentActivity() {
    private val pdfViewModel by viewModels<PdfViewModel>{
        viewModelFactory {
            addInitializer( PdfViewModel::class) {
                PdfViewModel(application)
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            splashScreen.setKeepOnScreenCondition{ pdfViewModel.isSplashScreen }
            DocScannerAppTheme() {
                Surface (
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ){
                    HomeScreen(pdfViewModel)
                }
            }
        }
    }
}

