@file:OptIn(ExperimentalMaterial3Api::class)
package com.example.escuela_msica_piendam.presentation.registro




import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.escuela_msica_piendam.domain.model.Categoria
import com.example.escuela_msica_piendam.presentation.common.BotonAtras
import com.example.escuela_msica_piendam.presentation.common.SelectorDesplegable
import com.example.escuela_msica_piendam.presentation.common.UiState


/**
 * Pantalla de registro de estudiantes.
 *
 * @param onRegistroExitoso se invoca una vez cuando el servidor crea la cuenta (la sesión ya quedó guardada).
 */
@Composable
fun RegistroScreen(
    onVolver: () -> Unit,
    onRegistroExitoso: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RegistroViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var mostrarContrasena by rememberSaveable { mutableStateOf(false) }

    val resultado = uiState.resultado
    LaunchedEffect(resultado) {
        if (resultado is UiState.Success) onRegistroExitoso()
    }

    val bloqueado = uiState.cargando

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(title = { Text("Crear cuenta") }, navigationIcon = { BotonAtras(onVolver) })
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = "Regístrate como estudiante de la escuela",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Campo(
                valor = uiState.nombre,
                onCambio = viewModel::onNombreChange,
                etiqueta = "Nombre completo",
                mensajeError = uiState.errores.nombre,
                enabled = !bloqueado,
            )
            Campo(
                valor = uiState.numCelular,
                onCambio = viewModel::onNumCelularChange,
                etiqueta = "Celular",
                mensajeError = uiState.errores.numCelular,
                enabled = !bloqueado,
                keyboardType = KeyboardType.Phone,
            )
            Campo(
                valor = uiState.contrasena,
                onCambio = viewModel::onContrasenaChange,
                etiqueta = "Contraseña",
                mensajeError = uiState.errores.contrasena,
                enabled = !bloqueado,
                keyboardType = KeyboardType.Password,
                esContrasena = true,
                mostrarContrasena = mostrarContrasena,
                onAlternarVisibilidad = { mostrarContrasena = !mostrarContrasena },
            )
            Campo(
                valor = uiState.confirmacion,
                onCambio = viewModel::onConfirmacionChange,
                etiqueta = "Confirmar contraseña",
                mensajeError = uiState.errores.confirmacion,
                enabled = !bloqueado,
                keyboardType = KeyboardType.Password,
                esContrasena = true,
                mostrarContrasena = mostrarContrasena,
                onAlternarVisibilidad = { mostrarContrasena = !mostrarContrasena },
            )

            SelectorDesplegable(
                etiqueta = "Categoría",
                seleccionado = uiState.categoria?.valorApi,
                opciones = Categoria.entries.map { it.valorApi },
                onSeleccion = { viewModel.onCategoriaChange(Categoria.desdeApi(it)) },
                enabled = !bloqueado,
                mensajeError = uiState.errores.categoria,
            )

            // Los instrumentos vienen del backend: Loading / Error / Success.
            when (val instrumentos = uiState.instrumentos) {
                is UiState.Loading -> Text("Cargando instrumentos…", style = MaterialTheme.typography.bodyMedium)
                is UiState.Error -> {
                    Text(instrumentos.mensaje, color = MaterialTheme.colorScheme.error)
                    TextButton(onClick = viewModel::cargarInstrumentos) { Text("Reintentar") }
                }
                is UiState.Success -> SelectorDesplegable(
                    etiqueta = "Instrumento",
                    seleccionado = uiState.instrumento,
                    opciones = instrumentos.datos,
                    onSeleccion = viewModel::onInstrumentoChange,
                    enabled = !bloqueado,
                    mensajeError = uiState.errores.instrumento,
                )
            }

            // Error del servidor (por ejemplo, celular ya registrado), sin bloquear la pantalla.
            if (resultado is UiState.Error) {
                Text(resultado.mensaje, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.error)
            }

            Button(
                onClick = viewModel::onRegistrarClick,
                enabled = !bloqueado,
                modifier = Modifier.fillMaxWidth().height(48.dp),
            ) {
                if (bloqueado) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary,
                    )
                } else {
                    Text("Registrarme")
                }
            }
        }
    }
}



/** Campo de texto del formulario; si es de contraseña, muestra puntos y el botón Mostrar/Ocultar. */
@Composable
private fun Campo(
    valor: String,
    onCambio: (String) -> Unit,
    etiqueta: String,
    mensajeError: String?,
    enabled: Boolean,
    keyboardType: KeyboardType = KeyboardType.Text,
    esContrasena: Boolean = false,
    mostrarContrasena: Boolean = false,
    onAlternarVisibilidad: () -> Unit = {},
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onCambio,
        label = { Text(etiqueta) },
        singleLine = true,
        enabled = enabled,
        isError = mensajeError != null,
        supportingText = mensajeError?.let { m -> { Text(m) } },
        visualTransformation = if (esContrasena && !mostrarContrasena) {
            PasswordVisualTransformation()
        } else {
            VisualTransformation.None
        },
        trailingIcon = if (esContrasena) {
            { TextButton(onClick = onAlternarVisibilidad) { Text(if (mostrarContrasena) "Ocultar" else "Mostrar") } }
        } else {
            null
        },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        modifier = Modifier.fillMaxWidth(),
    )
}