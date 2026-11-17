package com.example.escuela_msica_piendam.presentation.detalle


import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.escuela_msica_piendam.domain.model.Rol
import com.example.escuela_msica_piendam.domain.repository.AuthRepository
import com.example.escuela_msica_piendam.domain.usecase.CancelarInscripcionUseCase
import com.example.escuela_msica_piendam.domain.usecase.InscribirseUseCase
import com.example.escuela_msica_piendam.domain.usecase.ObtenerActividadUseCase
import com.example.escuela_msica_piendam.domain.usecase.ObtenerInscripcionesUseCase
import com.example.escuela_msica_piendam.presentation.common.UiState
import com.example.escuela_msica_piendam.presentation.navegation.Rutas
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class DetalleActividadViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val obtenerActividad: ObtenerActividadUseCase,
    private val obtenerInscripciones: ObtenerInscripcionesUseCase,
    private val inscribirse: InscribirseUseCase,
    private val cancelarInscripcion: CancelarInscripcionUseCase,
    private val authRepository: AuthRepository,
) : ViewModel() {

    // El id llega como argumento de navegación ("detalle/{idActividad}").
    private val idActividad: String = checkNotNull(savedStateHandle.get<String>(Rutas.ARG_ID_ACTIVIDAD))

    private val _uiState = MutableStateFlow(DetalleUiState())
    val uiState: StateFlow<DetalleUiState> = _uiState.asStateFlow()

    init { cargar(mostrarCarga = true) }

    fun reintentar() = cargar(mostrarCarga = true)

    fun mensajeMostrado() = _uiState.update { it.copy(mensaje = null) }

    /**
     * Un solo botón, dos acciones: si no hay inscripción => POST /api/inscripciones;
     * si ya hay => DELETE /api/inscripciones/{id}. Las reglas (categoría, cupo, duplicado)
     * las valida el backend y su motivo se muestra tal cual.
     */
    fun alternarInscripcion() {
        val estado = _uiState.value
        val contenido = estado.contenido
        if (contenido !is UiState.Success || estado.procesando) return
        val inscripcionActual = contenido.datos.inscripcion

        _uiState.update { it.copy(procesando = true) }
        viewModelScope.launch {
            val resultado = if (inscripcionActual == null) {
                inscribirse(idActividad).map { MENSAJE_INSCRITO }
            } else {
                cancelarInscripcion(inscripcionActual.id).map { MENSAJE_CANCELADO }
            }
            resultado
                .onSuccess { mensaje ->
                    _uiState.update { it.copy(procesando = false, mensaje = mensaje) }
                    cargar(mostrarCarga = false) // refresca el botón y el contador de cupos
                }
                .onFailure { e ->
                    _uiState.update { it.copy(procesando = false, mensaje = e.message ?: MENSAJE_GENERICO) }
                }
        }
    }

    private fun cargar(mostrarCarga: Boolean) {
        viewModelScope.launch {
            if (mostrarCarga) _uiState.update { it.copy(contenido = UiState.Loading) }

            val rol = authRepository.obtenerRol()
            val actividad = obtenerActividad(idActividad).getOrElse { e ->
                publicarError(e)
                return@launch
            }
            // Solo el estudiante necesita saber si ya está inscrito (el backend le devuelve solo las suyas).
            val inscripcion = if (rol == Rol.ESTUDIANTE) {
                obtenerInscripciones(idActividad).getOrElse { e ->
                    publicarError(e)
                    return@launch
                }.firstOrNull()
            } else {
                null
            }
            _uiState.update {
                it.copy(rol = rol, contenido = UiState.Success(DetalleActividad(actividad, inscripcion)))
            }
        }
    }

    /** Si ya había datos en pantalla, el error de una recarga se muestra como aviso sin borrarlos. */
    private fun publicarError(error: Throwable) {
        val mensaje = error.message ?: MENSAJE_GENERICO
        _uiState.update { estado ->
            if (estado.contenido is UiState.Success) {
                estado.copy(mensaje = mensaje)
            } else {
                estado.copy(contenido = UiState.Error(mensaje))
            }
        }
    }

    private companion object {
        const val MENSAJE_INSCRITO = "Inscripción confirmada"
        const val MENSAJE_CANCELADO = "Inscripción cancelada"
        const val MENSAJE_GENERICO = "No se pudo completar la operación. Inténtalo de nuevo."
    }
}