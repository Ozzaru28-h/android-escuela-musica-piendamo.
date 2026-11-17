@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.escuela_msica_piendam.presentation.detalle

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.escuela_msica_piendam.domain.model.Actividad
import com.example.escuela_msica_piendam.domain.model.Rol
import com.example.escuela_msica_piendam.presentation.common.BotonAtras
import com.example.escuela_msica_piendam.presentation.common.EstadoCargando
import com.example.escuela_msica_piendam.presentation.common.EstadoError
import com.example.escuela_msica_piendam.presentation.common.UiState

@Composable
fun DetalleActividadScreen(
    onVolver: () -> Unit,
    onIrAAsistencia: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DetalleActividadViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // Aviso de una sola vez: éxito, o el motivo del rechazo que explicó el backend (409, 403...).
    val mensaje = uiState.mensaje
    LaunchedEffect(mensaje) {
        if (mensaje != null) {
            snackbarHostState.showSnackbar(mensaje)
            viewModel.mensajeMostrado()
        }
    }

    val contenido = uiState.contenido

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(title = { Text("Detalle de la clase") }, navigationIcon = { BotonAtras(onVolver) })
        },
        bottomBar = {
            if (contenido is UiState.Success) {
                BarraAccion(
                    rol = uiState.rol,
                    detalle = contenido.datos,
                    procesando = uiState.procesando,
                    onAlternarInscripcion = viewModel::alternarInscripcion,
                    onIrAAsistencia = { onIrAAsistencia(contenido.datos.actividad.id) },
                )
            }
        },
    ) { padding ->
        when (contenido) {
            is UiState.Loading -> EstadoCargando(Modifier.padding(padding))
            is UiState.Error -> EstadoError(contenido.mensaje, viewModel::reintentar, Modifier.padding(padding))
            is UiState.Success -> ContenidoDetalle(contenido.datos.actividad, Modifier.padding(padding))
        }
    }
}

@Composable
private fun ContenidoDetalle(actividad: Actividad, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = actividad.categoria.valorApi.uppercase(),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
        )
        Text(text = actividad.titulo, style = MaterialTheme.typography.headlineSmall)
        Text(text = actividad.descripcion, style = MaterialTheme.typography.bodyLarge)
        Dato("Fecha y hora", "${actividad.fecha} · ${actividad.hora}")
        Dato("Instrumento", actividad.instrumentoDirigido)
        actividad.lugar?.let { Dato("Lugar", it) }
        actividad.indicacionesVestimenta?.let { Dato("Vestimenta", it) }
        Dato(
            "Cupos",
            actividad.cupoMaximo?.let { "${actividad.inscritos} de $it inscritos" }
                ?: "${actividad.inscritos} inscritos (sin límite)",
        )
    }
}

@Composable
private fun Dato(etiqueta: String, valor: String) {
    Column {
        Text(text = etiqueta, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = valor, style = MaterialTheme.typography.bodyLarge)
    }
}

/** Estudiante: Inscribirse <-> Cancelar inscripción. Director: acceso al control de asistencia. */
@Composable
private fun BarraAccion(
    rol: Rol?,
    detalle: DetalleActividad,
    procesando: Boolean,
    onAlternarInscripcion: () -> Unit,
    onIrAAsistencia: () -> Unit,
) {
    when (rol) {
        Rol.ESTUDIANTE -> {
            val inscrito = detalle.inscripcion != null
            BottomAppBar {
                Button(
                    onClick = onAlternarInscripcion,
                    enabled = !procesando && (inscrito || detalle.actividad.tieneCupo),
                    colors = if (inscrito) {
                        ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error,
                            contentColor = MaterialTheme.colorScheme.onError,
                        )
                    } else {
                        ButtonDefaults.buttonColors()
                    },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                ) {
                    if (procesando) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = LocalContentColor.current,
                        )
                    } else {
                        Text(
                            when {
                                inscrito -> "Cancelar inscripción"
                                detalle.actividad.tieneCupo -> "Inscribirse"
                                else -> "Sin cupos"
                            },
                        )
                    }
                }
            }
        }
        Rol.DIRECTOR -> BottomAppBar {
            Button(
                onClick = onIrAAsistencia,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            ) { Text("Control de asistencia") }
        }
        null -> Unit
    }
}