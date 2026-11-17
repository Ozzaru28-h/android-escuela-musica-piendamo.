package com.example.escuela_msica_piendam.presentation.detalle


import com.example.escuela_msica_piendam.domain.model.Actividad
import com.example.escuela_msica_piendam.domain.model.Inscripcion
import com.example.escuela_msica_piendam.domain.model.Rol
import com.example.escuela_msica_piendam.presentation.common.UiState

/** La actividad y, si el usuario es estudiante y ya se inscribió, su inscripción (null = no inscrito). */
data class DetalleActividad(
    val actividad: Actividad,
    val inscripcion: Inscripcion?,
)

/**
 * @property procesando hay una inscripción/cancelación en curso (el botón muestra progreso).
 * @property mensaje aviso de una sola vez (éxito o motivo del rechazo) que se muestra en Snackbar.
 */
data class DetalleUiState(
    val contenido: UiState<DetalleActividad> = UiState.Loading,
    val rol: Rol? = null,
    val procesando: Boolean = false,
    val mensaje: String? = null,
)