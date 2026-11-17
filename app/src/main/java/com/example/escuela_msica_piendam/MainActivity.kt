package com.example.escuela_msica_piendam


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.escuela_msica_piendam.presentation.MainViewModel
import com.example.escuela_msica_piendam.presentation.navegation.AppNavHost
import dagger.hilt.android.AndroidEntryPoint

/**
 * Única Activity de la app. @AndroidEntryPoint le permite a Hilt inyectar
 * ViewModels en las pantallas Compose que viven dentro de ella.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    val destinoState by mainViewModel.destinoInicial.collectAsStateWithLifecycle()
                    val destino = destinoState // copia local para poder hacer smart cast

                    if (destino == null) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center,
                        ) {
                            CircularProgressIndicator()
                        }
                    } else {
                        AppNavHost(destinoInicial = destino)
                    }
                }
            }
        }
    }
}