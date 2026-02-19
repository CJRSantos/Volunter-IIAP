package com.gdcj.voluntariadoiiap

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.gdcj.voluntariadoiiap.navigation.AppNavigation
import com.gdcj.voluntariadoiiap.ui.theme.VOLUNTARIADOIIAPTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VOLUNTARIADOIIAPTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // Agregamos un Modifier.padding(innerPadding) si es necesario dentro de AppNavigation
                    // o lo manejamos directamente en las pantallas.
                    AppNavigation()
                }
            }
        }
    }
}
