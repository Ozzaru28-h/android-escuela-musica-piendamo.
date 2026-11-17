@file:OptIn(ExperimentalMaterial3Api::class)
package com.example.escuela_msica_piendam.presentation.crear
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.escuela_msica_piendam.domain.model.Categoria
import com.example.escuela_msica_piendam.presentation.common.BotonAtras
import com.example.escuela_msica_piendam.presentation.common.SelectorDesplegable
import com.example.escuela_msica_piendam.presentation.common.SelectorFecha
import com.example.escuela_msica_piendam.presentation.common.SelectorHora

import com.example.escuela_msica_piendam.presentation.common.UiState

@Composable
fun CrearActividadScreen(
    onVolver: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CrearActividadViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val resultado = uiState.resultado
    val bloqueado = uiState.guardando

    // Éxito: aviso en Snackbar (el formulario ya quedó limpio desde el ViewModel).
    LaunchedEffect(resultado) {
        if (resultado is UiState.Success) {
            snackbarHostState.showSnackbar("Actividad publicada correctamente")
            viewModel.limpiarResultado()
        }
    }

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(title = { Text("Nueva actividad") }, navigationIcon = { BotonAtras(onVolver) })
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            CampoTexto(uiState.titulo, viewModel::onTituloChange, "Título", uiState.errores.titulo, !bloqueado)
            CampoTexto(
                uiState.descripcion, viewModel::onDescripcionChange, "Descripción",
                uiState.errores.descripcion, !bloqueado, singleLine = false, minLines = 3,
            )

            SelectorDesplegable(
                etiqueta = "Categoría",
                seleccionado = uiState.categoria?.valorApi,
                opciones = Categoria.entries.map { it.valorApi },
                onSeleccion = { viewModel.onCategoriaChange(Categoria.desdeApi(it)) },
                enabled = !bloqueado,
                mensajeError = uiState.errores.categoria,
            )

            // Las opciones de instrumento vienen del backend: Loading / Error / Success.
            when (val instrumentos = uiState.instrumentos) {
                is UiState.Loading -> Text("Cargando instrumentos…", style = MaterialTheme.typography.bodyMedium)
                is UiState.Error -> {
                    Text(instrumentos.mensaje, color = MaterialTheme.colorScheme.error)
                    TextButton(onClick = viewModel::cargarInstrumentos) { Text("Reintentar") }
                }
                is UiState.Success -> SelectorDesplegable(
                    etiqueta = "Instrumento dirigido",
                    seleccionado = uiState.instrumento,
                    opciones = instrumentos.datos,
                    onSeleccion = viewModel::onInstrumentoChange,
                    enabled = !bloqueado,
                    mensajeError = uiState.errores.instrumento,
                )
            }

            SelectorFecha("Fecha", uiState.fecha, viewModel::onFechaChange, enabled = !bloqueado, mensajeError = uiState.errores.fecha)
            SelectorHora("Hora", uiState.hora, viewModel::onHoraChange, enabled = !bloqueado, mensajeError = uiState.errores.hora)

            CampoTexto(uiState.lugar, viewModel::onLugarChange, "Lugar (opcional)", null, !bloqueado)
            CampoTexto(uiState.indicaciones, viewModel::onIndicacionesChange, "Vestimenta (opcional)", null, !bloqueado)
            CampoTexto(
                uiState.cupo, viewModel::onCupoChange, "Cupo máximo (opcional)",
                uiState.errores.cupo, !bloqueado, keyboardType = KeyboardType.Number,
            )

            // Error del servidor (por ejemplo 403 si no eres director, o un 400 con detalles).
            if (resultado is UiState.Error) {
                Text(resultado.mensaje, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.error)
            }

            Button(
                onClick = viewModel::onGuardarClick,
                enabled = !bloqueado,
                modifier = Modifier.fillMaxWidth().height(48.dp),
            ) {
                if (bloqueado) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary,
                    )
                } else {
                    Text("Guardar")
                }
            }
        }
    }
}

@Composable
private fun CampoTexto(
    valor: String,
    onCambio: (String) -> Unit,
    etiqueta: String,
    mensajeError: String?,
    enabled: Boolean,
    singleLine: Boolean = true,
    minLines: Int = 1,
    keyboardType: KeyboardType = KeyboardType.Text,
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onCambio,
        label = { Text(etiqueta) },
        isError = mensajeError != null,
        supportingText = mensajeError?.let { m -> { Text(m) } },
        enabled = enabled,
        singleLine = singleLine,
        minLines = minLines,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        modifier = Modifier.fillMaxWidth(),
    )
}