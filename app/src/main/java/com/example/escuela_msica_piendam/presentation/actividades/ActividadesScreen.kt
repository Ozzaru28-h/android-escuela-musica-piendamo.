@file:OptIn(ExperimentalMaterial3Api::class)
package com.example.escuela_msica_piendam.presentation.actividades

import androidx.compose.material3.TextButton
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.escuela_msica_piendam.domain.model.Actividad
import com.example.escuela_msica_piendam.domain.model.Categoria
import com.example.escuela_msica_piendam.presentation.common.UiState
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect

/**
 * Pantalla de lista de actividades (HU-03).
 * Solo DIBUJA el estado que le entrega el ViewModel y le avisa de las acciones del usuario.
 * No contiene lógica de negocio ni llamadas de red.
 *
 * @param onActividadClick recibe el id de la actividad tocada (la navegación al detalle se conecta después).
 */
@Composable
fun ActividadesScreen(
    modifier: Modifier = Modifier,
    viewModel: ActividadesViewModel = hiltViewModel(),
    onActividadClick: (String) -> Unit = {},
    onCerrarSesion: () -> Unit,
    onNuevaActividad: () -> Unit,
) {
    // Se suscribe al StateFlow; cada cambio redibuja la pantalla y se pausa en segundo plano.
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) { viewModel.recargar() }

    Scaffold(
        modifier = modifier,
        topBar = { /* igual que antes (con el botón "Salir") */ },
        floatingActionButton = {
            if (uiState.esDirector) {
                ExtendedFloatingActionButton(onClick = onNuevaActividad) { Text("Nueva actividad") }
            }
        },
    ) { paddingInterno ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingInterno),
        ) {
            // Los chips se muestran SIEMPRE: así el usuario puede cambiar de filtro
            // aunque la carga anterior haya fallado.
            FiltroCategorias(
                seleccionada = uiState.categoriaSeleccionada,
                onSeleccionar = viewModel::seleccionarCategoria,
            )

            // El `when` sobre una clase sellada obliga a cubrir los tres estados.
            when (val estado = uiState.actividades) {
                is UiState.Loading -> Cargando()
                is UiState.Error -> MensajeError(
                    mensaje = estado.mensaje,
                    onReintentar = viewModel::reintentar,
                )
                is UiState.Success -> ListaActividades(
                    actividades = estado.datos,
                    onActividadClick = onActividadClick,
                )
            }
        }
    }
}

/** Fila de chips: "Todas" (sin filtro) + una por cada categoría. */
@Composable
private fun FiltroCategorias(
    seleccionada: Categoria?,
    onSeleccionar: (Categoria?) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        ChipCategoria(
            texto = "Todas",
            seleccionado = seleccionada == null,
            onClick = { onSeleccionar(null) },
        )
        Categoria.entries.forEach { categoria ->
            ChipCategoria(
                texto = categoria.valorApi,
                seleccionado = seleccionada == categoria,
                onClick = { onSeleccionar(categoria) },
            )
        }
    }
}

@Composable
private fun ChipCategoria(
    texto: String,
    seleccionado: Boolean,
    onClick: () -> Unit,
) {
    FilterChip(
        selected = seleccionado,
        onClick = onClick,
        label = { Text(texto) },
        leadingIcon = if (seleccionado) {
            {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = null,
                    modifier = Modifier.size(FilterChipDefaults.IconSize),
                )
            }
        } else {
            null
        },
    )
}

@Composable
private fun Cargando() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator()
    }
}

/** Mensaje de error claro + botón para reintentar: el usuario nunca queda bloqueado. */
@Composable
private fun MensajeError(
    mensaje: String,
    onReintentar: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = mensaje,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onReintentar) {
            Text("Reintentar")
        }
    }
}

@Composable
private fun ListaActividades(
    actividades: List<Actividad>,
    onActividadClick: (String) -> Unit,
) {
    if (actividades.isEmpty()) {
        // Lista vacía NO es un error: es el caso "no hay clases para esta categoría" (HU-02, CA-03).
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "No hay clases disponibles para esta categoría.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 88.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // `key` permite a Compose reconocer cada tarjeta al cambiar el filtro.
            items(items = actividades, key = { it.id }) { actividad ->
                TarjetaActividad(
                    actividad = actividad,
                    onClick = { onActividadClick(actividad.id) },
                )
            }
        }
    }
}

@Composable
private fun TarjetaActividad(
    actividad: Actividad,
    onClick: () -> Unit,
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = actividad.categoria.valorApi.uppercase(),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = actividad.titulo,
                style = MaterialTheme.typography.titleMedium,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = actividad.descripcion,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "${actividad.fecha} · ${actividad.hora}",
                style = MaterialTheme.typography.bodySmall,
            )
            actividad.lugar?.let { lugar ->
                Text(text = lugar, style = MaterialTheme.typography.bodySmall)
            }
            Text(
                text = "Para: ${actividad.instrumentoDirigido}",
                style = MaterialTheme.typography.bodySmall,
            )
            InfoCupos(actividad)
        }
    }
}

/** Muestra "Inscritos: 18 de 45", o "Cupo lleno" en rojo. Si no hay límite de cupos, no muestra nada. */
@Composable
private fun InfoCupos(actividad: Actividad) {
    val cupoMaximo = actividad.cupoMaximo ?: return

    if (actividad.tieneCupo) {
        Text(
            text = "Inscritos: ${actividad.inscritos} de $cupoMaximo",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    } else {
        Text(
            text = "Cupo lleno ($cupoMaximo de $cupoMaximo)",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.error,
        )
    }
}