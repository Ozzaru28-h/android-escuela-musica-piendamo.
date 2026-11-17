package com.example.escuela_msica_piendam.presentation.crear


import com.example.escuela_msica_piendam.domain.model.Actividad
import com.example.escuela_msica_piendam.domain.model.Categoria
import com.example.escuela_msica_piendam.presentation.common.UiState

data class ErroresFormulario(
    val titulo: String? = null,
    val descripcion: String? = null,
    val categoria: String? = null,
    val instrumento: String? = null,
    val fecha: String? = null,
    val hora: String? = null,
    val cupo: String? = null,
) {
    val hayErrores: Boolean
        get() = listOfNotNull(titulo, descripcion, categoria, instrumento, fecha, hora, cupo).isNotEmpty()
}

/**
 * @property instrumentos opciones del selector (vienen del backend).
 * @property resultado null = aún no se guarda; Loading / Success / Error después de pulsar "Guardar".
 */
data class CrearActividadUiState(
    val titulo: String = "",
    val descripcion: String = "",
    val categoria: Categoria? = null,
    val instrumento: String? = null,
    val fecha: String? = null,
    val hora: String? = null,
    val lugar: String = "",
    val indicaciones: String = "",
    val cupo: String = "",
    val errores: ErroresFormulario = ErroresFormulario(),
    val instrumentos: UiState<List<String>> = UiState.Loading,
    val resultado: UiState<Actividad>? = null,
) {
    val guardando: Boolean
        get() = resultado is UiState.Loading
}