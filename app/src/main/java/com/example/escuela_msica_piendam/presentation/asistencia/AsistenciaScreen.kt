@file:OptIn(ExperimentalMaterial3Api::class)
package com.example.escuela_msica_piendam.presentation.asistencia




import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.escuela_msica_piendam.domain.model.EstadoAsistencia
import com.example.escuela_msica_piendam.domain.model.Inscripcion
import com.example.escuela_msica_piendam.presentation.common.BotonAtras
import com.example.escuela_msica_piendam.presentation.common.EstadoCargando
import com.example.escuela_msica_piendam.presentation.common.EstadoError
import com.example.escuela_msica_piendam.presentation.common.UiState

/** Lo que ve el director (Presente/Ausente) y el valor que entiende el backend. */
private val OPCIONES = listOf(
    EstadoAsistencia.ASISTIO to "Presente",
    EstadoAsistencia.FALTO to "Ausente",
)

@Composable
fun AsistenciaScreen(
    onVolver: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AsistenciaViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    val mensaje = uiState.mensaje
    LaunchedEffect(mensaje) {
        if (mensaje != null) {
            snackbarHostState.showSnackbar(mensaje)
            viewModel.mensajeMostrado()
        }
    }

    val lista = uiState.lista

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(title = { Text("Control de asistencia") }, navigationIcon = { BotonAtras(onVolver) })
        },
    ) { padding ->
        when (lista) {
            is UiState.Loading -> EstadoCargando(Modifier.padding(padding))
            is UiState.Error -> EstadoError(lista.mensaje, viewModel::cargar, Modifier.padding(padding))
            is UiState.Success -> ListaAsistencia(
                inscripciones = lista.datos,
                enProceso = uiState.enProceso,
                onMarcar = viewModel::marcarAsistencia,
                modifier = Modifier.padding(padding),
            )
        }
    }
}

@Composable
private fun ListaAsistencia(
    inscripciones: List<Inscripcion>,
    enProceso: Set<String>,
    onMarcar: (Inscripcion, EstadoAsistencia) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (inscripciones.isEmpty()) {
        Box(modifier = modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
            Text("Aún no hay estudiantes inscritos en esta actividad.", style = MaterialTheme.typography.bodyLarge)
        }
        return
    }

    // El resumen se calcula con la lista en pantalla: se actualiza al instante tras cada cambio.
    val presentes = inscripciones.count { it.estadoAsistencia == EstadoAsistencia.ASISTIO }
    val ausentes = inscripciones.count { it.estadoAsistencia == EstadoAsistencia.FALTO }
    val pendientes = inscripciones.count { it.estadoAsistencia == EstadoAsistencia.PENDIENTE }

    Column(modifier = modifier.fillMaxSize()) {
        Text(
            text = "Presentes: $presentes · Ausentes: $ausentes · Pendientes: $pendientes",
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(16.dp),
        )
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(items = inscripciones, key = { it.id }) { inscripcion ->
                FilaEstudiante(
                    inscripcion = inscripcion,
                    bloqueada = inscripcion.id in enProceso,
                    onMarcar = { estado -> onMarcar(inscripcion, estado) },
                )
            }
        }
    }
}

@Composable
private fun FilaEstudiante(
    inscripcion: Inscripcion,
    bloqueada: Boolean,
    onMarcar: (EstadoAsistencia) -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = inscripcion.nombreEstudiante, style = MaterialTheme.typography.titleMedium)
            Text(
                text = listOfNotNull(inscripcion.categoriaEstudiante, inscripcion.instrumentoEstudiante)
                    .joinToString(" · "),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Row(
                modifier = Modifier.selectableGroup().padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(24.dp),
            ) {
                OPCIONES.forEach { (estado, etiqueta) ->
                    val seleccionado = inscripcion.estadoAsistencia == estado
                    Row(
                        modifier = Modifier.selectable(
                            selected = seleccionado,
                            enabled = !bloqueada,
                            role = Role.RadioButton,
                            onClick = { onMarcar(estado) },
                        ),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        // onClick = null: el toque lo maneja la fila completa (mejor accesibilidad).
                        RadioButton(selected = seleccionado, onClick = null, enabled = !bloqueada)
                        Text(text = etiqueta, modifier = Modifier.padding(start = 8.dp))
                    }
                }
            }
            if (inscripcion.estadoAsistencia == EstadoAsistencia.JUSTIFICADO) {
                Text("Falta justificada", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}