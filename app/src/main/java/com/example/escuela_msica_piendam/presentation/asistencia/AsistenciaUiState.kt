package com.example.escuela_msica_piendam.presentation.asistencia

import com.example.escuela_msica_piendam.domain.model.Inscripcion
import com.example.escuela_msica_piendam.presentation.common.UiState

/**
 * @property enProceso ids de inscripciones con un PATCH en curso (su fila queda bloqueada).
 * @property mensaje aviso de una sola vez cuando falla una actualización.
 */
data class AsistenciaUiState(
    val lista: UiState<List<Inscripcion>> = UiState.Loading,
    val enProceso: Set<String> = emptySet(),
    val mensaje: String? = null,
)