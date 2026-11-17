package com.example.escuela_msica_piendam.presentation.asistencia

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.escuela_msica_piendam.domain.model.EstadoAsistencia
import com.example.escuela_msica_piendam.domain.model.Inscripcion
import com.example.escuela_msica_piendam.domain.usecase.ObtenerInscripcionesUseCase
import com.example.escuela_msica_piendam.domain.usecase.RegistrarAsistenciaUseCase
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
class AsistenciaViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val obtenerInscripciones: ObtenerInscripcionesUseCase,
    private val registrarAsistencia: RegistrarAsistenciaUseCase,
) : ViewModel() {

    private val idActividad: String = checkNotNull(savedStateHandle.get<String>(Rutas.ARG_ID_ACTIVIDAD))

    private val _uiState = MutableStateFlow(AsistenciaUiState())
    val uiState: StateFlow<AsistenciaUiState> = _uiState.asStateFlow()

    init { cargar() }

    fun cargar() {
        viewModelScope.launch {
            _uiState.update { it.copy(lista = UiState.Loading) }
            obtenerInscripciones(idActividad)
                .onSuccess { lista -> _uiState.update { it.copy(lista = UiState.Success(lista)) } }
                .onFailure { e ->
                    _uiState.update { it.copy(lista = UiState.Error(e.message ?: MENSAJE_GENERICO)) }
                }
        }
    }

    fun mensajeMostrado() = _uiState.update { it.copy(mensaje = null) }

    /** PATCH /api/inscripciones/{id}/asistencia. Solo cambia la fila tocada cuando el servidor confirma. */
    fun marcarAsistencia(inscripcion: Inscripcion, estado: EstadoAsistencia) {
        if (inscripcion.estadoAsistencia == estado || inscripcion.id in _uiState.value.enProceso) return

        _uiState.update { it.copy(enProceso = it.enProceso + inscripcion.id) }
        viewModelScope.launch {
            registrarAsistencia(inscripcion.id, estado)
                .onSuccess { actualizada ->
                    _uiState.update {
                        it.copy(
                            lista = reemplazar(it.lista, actualizada),
                            enProceso = it.enProceso - inscripcion.id,
                        )
                    }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(
                            enProceso = it.enProceso - inscripcion.id,
                            mensaje = e.message ?: MENSAJE_GENERICO,
                        )
                    }
                }
        }
    }

    private fun reemplazar(
        lista: UiState<List<Inscripcion>>,
        actualizada: Inscripcion,
    ): UiState<List<Inscripcion>> =
        if (lista is UiState.Success) {
            UiState.Success(lista.datos.map { if (it.id == actualizada.id) actualizada else it })
        } else {
            lista
        }

    private companion object {
        const val MENSAJE_GENERICO = "No se pudo actualizar la asistencia. Inténtalo de nuevo."
    }
}