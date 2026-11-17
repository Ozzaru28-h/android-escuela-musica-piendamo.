package com.example.escuela_msica_piendam.presentation.auth

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.escuela_msica_piendam.presentation.common.UiState


/**
 * Pantalla de inicio de sesión. Solo dibuja el estado del ViewModel y le reporta eventos.
 *
 * @param onLoginExitoso se invoca una vez cuando el servidor acepta las credenciales.
 */
@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = hiltViewModel(),
    onLoginExitoso: () -> Unit,
    onIrARegistro: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // rememberSaveable: el estado "mostrar/ocultar" sobrevive a rotaciones.
    var mostrarContrasena by rememberSaveable { mutableStateOf(false) }

    // Efecto secundario: navegar cuando el resultado pasa a Success.
    val resultado = uiState.resultado
    LaunchedEffect(resultado) {
        if (resultado is UiState.Success) onLoginExitoso()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .safeDrawingPadding() // respeta barra de estado, navegación y teclado
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        Text(
            text = "Escuela de Música Piendamó",
            style = MaterialTheme.typography.headlineMedium,
        )
        Text(
            text = "Inicia sesión para continuar",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = uiState.numCelular,
            onValueChange = viewModel::onNumCelularChange,
            label = { Text("Celular") },
            singleLine = true,
            enabled = !uiState.cargando,
            isError = uiState.errorCelular != null,
            supportingText = uiState.errorCelular?.let { mensaje -> { Text(mensaje) } },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Phone,
                imeAction = ImeAction.Next,
            ),
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = uiState.contrasena,
            onValueChange = viewModel::onContrasenaChange,
            label = { Text("Contraseña") },
            singleLine = true,
            enabled = !uiState.cargando,
            isError = uiState.errorContrasena != null,
            supportingText = uiState.errorContrasena?.let { mensaje -> { Text(mensaje) } },
            visualTransformation = if (mostrarContrasena) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation() // muestra puntos en vez de las letras
            },
            trailingIcon = {
                TextButton(onClick = { mostrarContrasena = !mostrarContrasena }) {
                    Text(if (mostrarContrasena) "Ocultar" else "Mostrar")
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done,
            ),
            keyboardActions = KeyboardActions(onDone = { viewModel.onIngresarClick() }),
            modifier = Modifier.fillMaxWidth(),
        )

        // Error del servidor (por ejemplo "Celular o contraseña incorrectos"), sin bloquear la pantalla.
        if (resultado is UiState.Error) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = resultado.mensaje,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error,
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = viewModel::onIngresarClick,
            enabled = !uiState.cargando,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
        ) {
            if (uiState.cargando) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimary,
                )
            } else {
                Text("Ingresar")
            }
            TextButton(
                onClick = onIrARegistro,
                enabled = !uiState.cargando,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("¿No tienes cuenta? Regístrate")
            }
        }
    }
}