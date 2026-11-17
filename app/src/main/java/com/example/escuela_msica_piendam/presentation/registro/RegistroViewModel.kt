package com.example.escuela_msica_piendam.presentation.registro


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.escuela_msica_piendam.domain.model.Categoria
import com.example.escuela_msica_piendam.domain.model.NuevoUsuario
import com.example.escuela_msica_piendam.domain.usecase.ObtenerInstrumentosCatalogoUseCase
import com.example.escuela_msica_piendam.domain.usecase.RegistrarUsuarioUseCase
import com.example.escuela_msica_piendam.presentation.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class RegistroViewModel @Inject constructor(
    private val registrarUsuario: RegistrarUsuarioUseCase,
    private val obtenerInstrumentos: ObtenerInstrumentosCatalogoUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegistroUiState())
    val uiState: StateFlow<RegistroUiState> = _uiState.asStateFlow()

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
    fun onNombreChange(v: String) = editar { it.copy(nombre = v, errores = it.errores.copy(nombre = null)) }

    fun onNumCelularChange(v: String) = editar {
        // Solo dígitos y máximo 10: igual que la regla del backend.
        it.copy(numCelular = v.filter { c -> c.isDigit() }.take(LONGITUD_CELULAR), errores = it.errores.copy(numCelular = null))
    }

    fun onContrasenaChange(v: String) = editar { it.copy(contrasena = v, errores = it.errores.copy(contrasena = null)) }
    fun onConfirmacionChange(v: String) = editar { it.copy(confirmacion = v, errores = it.errores.copy(confirmacion = null)) }
    fun onCategoriaChange(v: Categoria) = editar { it.copy(categoria = v, errores = it.errores.copy(categoria = null)) }
    fun onInstrumentoChange(v: String) = editar { it.copy(instrumento = v, errores = it.errores.copy(instrumento = null)) }

    fun onRegistrarClick() {
        val estado = _uiState.value
        if (estado.cargando) return // evita doble toque

        val categoria = estado.categoria
        val instrumento = estado.instrumento

        // Validación local para ahorrar una petición; la definitiva sigue siendo la del backend.
        val errores = ErroresRegistro(
            nombre = if (estado.nombre.trim().length < MIN_NOMBRE) "Escribe tu nombre completo" else null,
            numCelular = if (estado.numCelular.length != LONGITUD_CELULAR) {
                "El celular debe tener $LONGITUD_CELULAR dígitos"
            } else {
                null
            },
            contrasena = if (estado.contrasena.length < MIN_CONTRASENA) {
                "La contraseña debe tener al menos $MIN_CONTRASENA caracteres"
            } else {
                null
            },
            confirmacion = if (estado.confirmacion != estado.contrasena) "Las contraseñas no coinciden" else null,
            categoria = if (categoria == null) "Elige tu categoría" else null,
            instrumento = if (instrumento == null) "Elige tu instrumento" else null,
        )
        if (errores.hayErrores || categoria == null || instrumento == null) {
            _uiState.update { it.copy(errores = errores) }
            return
        }

        _uiState.update { it.copy(errores = ErroresRegistro(), resultado = UiState.Loading) }

        viewModelScope.launch {
            registrarUsuario(
                NuevoUsuario(
                    nombre = estado.nombre,
                    numCelular = estado.numCelular,
                    contrasena = estado.contrasena,
                    categoria = categoria,
                    instrumento = instrumento,
                ),
            )
                .onSuccess { usuario -> _uiState.update { it.copy(resultado = UiState.Success(usuario)) } }
                .onFailure { e ->
                    // Ej.: 409 "Ya existe un usuario registrado con ese celular" (mensaje del backend).
                    _uiState.update { it.copy(resultado = UiState.Error(e.message ?: MENSAJE_GENERICO)) }
                }
        }
    }

    private fun editar(cambio: (RegistroUiState) -> RegistroUiState) {
        _uiState.update { cambio(it).copy(resultado = null) }
    }

    private companion object {
        const val LONGITUD_CELULAR = 10
        const val MIN_NOMBRE = 2
        const val MIN_CONTRASENA = 6
        const val MENSAJE_GENERICO = "No se pudo completar el registro. Inténtalo de nuevo."
    }
}