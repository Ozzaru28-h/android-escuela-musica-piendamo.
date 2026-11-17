package com.example.escuela_msica_piendam.presentation.crear

import com.example.escuela_msica_piendam.domain.usecase.CrearActividadUseCase
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.escuela_msica_piendam.domain.model.Categoria
import com.example.escuela_msica_piendam.domain.model.NuevaActividad

import com.example.escuela_msica_piendam.domain.usecase.ObtenerInstrumentosUseCase
import com.example.escuela_msica_piendam.presentation.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class CrearActividadViewModel @Inject constructor(
    private val crearActividad: CrearActividadUseCase,
    private val obtenerInstrumentos: ObtenerInstrumentosUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(CrearActividadUiState())
    val uiState: StateFlow<CrearActividadUiState> = _uiState.asStateFlow()

    init { cargarInstrumentos() }

    fun cargarInstrumentos() {
        viewModelScope.launch {
            _uiState.update { it.copy(instrumentos = UiState.Loading) }
            obtenerInstrumentos()
                .onSuccess { lista -> _uiState.update { it.copy(instrumentos = UiState.Success(lista)) } }
                .onFailure { e ->
                    _uiState.update { it.copy(instrumentos = UiState.Error(e.message ?: MENSAJE_GENERICO)) }
                }
        }
    }

    // Cada cambio limpia el error de ese campo y el resultado anterior.
    fun onTituloChange(v: String) = editar { it.copy(titulo = v, errores = it.errores.copy(titulo = null)) }
    fun onDescripcionChange(v: String) = editar { it.copy(descripcion = v, errores = it.errores.copy(descripcion = null)) }
    fun onCategoriaChange(v: Categoria) = editar { it.copy(categoria = v, errores = it.errores.copy(categoria = null)) }
    fun onInstrumentoChange(v: String) = editar { it.copy(instrumento = v, errores = it.errores.copy(instrumento = null)) }
    fun onFechaChange(v: String) = editar { it.copy(fecha = v, errores = it.errores.copy(fecha = null)) }
    fun onHoraChange(v: String) = editar { it.copy(hora = v, errores = it.errores.copy(hora = null)) }
    fun onLugarChange(v: String) = editar { it.copy(lugar = v) }
    fun onIndicacionesChange(v: String) = editar { it.copy(indicaciones = v) }
    fun onCupoChange(v: String) =
        editar { it.copy(cupo = v.filter { c -> c.isDigit() }.take(4), errores = it.errores.copy(cupo = null)) }

    /** La pantalla ya mostró el mensaje de éxito. */
    fun limpiarResultado() = _uiState.update { it.copy(resultado = null) }

    fun onGuardarClick() {
        val estado = _uiState.value
        if (estado.guardando) return

        val categoria = estado.categoria
        val instrumento = estado.instrumento
        val fecha = estado.fecha
        val hora = estado.hora
        val cupo = estado.cupo.trim().toIntOrNull()
        val cupoInvalido = estado.cupo.isNotBlank() && (cupo == null || cupo < 1)

        // Validación local para ahorrar una petición; la definitiva sigue siendo la del backend.
        val errores = ErroresFormulario(
            titulo = if (estado.titulo.trim().length < MIN_TITULO) "El título debe tener al menos $MIN_TITULO caracteres" else null,
            descripcion = if (estado.descripcion.isBlank()) "La descripción es obligatoria" else null,
            categoria = if (categoria == null) "Elige una categoría" else null,
            instrumento = if (instrumento == null) "Elige un instrumento" else null,
            fecha = if (fecha == null) "Elige una fecha" else null,
            hora = if (hora == null) "Elige una hora" else null,
            cupo = if (cupoInvalido) "El cupo debe ser un número entero mayor que 0" else null,
        )
        if (errores.hayErrores || categoria == null || instrumento == null || fecha == null || hora == null) {
            _uiState.update { it.copy(errores = errores) }
            return
        }

        _uiState.update { it.copy(errores = ErroresFormulario(), resultado = UiState.Loading) }

        viewModelScope.launch {
            crearActividad(
                NuevaActividad(
                    titulo = estado.titulo,
                    descripcion = estado.descripcion,
                    categoria = categoria,
                    instrumentoDirigido = instrumento,
                    fecha = fecha,
                    hora = hora,
                    lugar = estado.lugar,
                    indicacionesVestimenta = estado.indicaciones,
                    cupoMaximo = cupo,
                ),
            )
                .onSuccess { actividad ->
                    // Éxito: formulario limpio (se conservan las opciones del selector) + aviso.
                    _uiState.update {
                        CrearActividadUiState(instrumentos = it.instrumentos, resultado = UiState.Success(actividad))
                    }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(resultado = UiState.Error(e.message ?: MENSAJE_GENERICO)) }
                }
        }
    }

    private fun editar(cambio: (CrearActividadUiState) -> CrearActividadUiState) {
        _uiState.update { cambio(it).copy(resultado = null) }
    }

    private companion object {
        const val MIN_TITULO = 3
        const val MENSAJE_GENERICO = "No se pudo completar la operación. Inténtalo de nuevo."
    }
}