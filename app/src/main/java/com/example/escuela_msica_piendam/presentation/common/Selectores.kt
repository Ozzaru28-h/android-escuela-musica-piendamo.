@file:OptIn(ExperimentalMaterial3Api::class)
package com.example.escuela_msica_piendam.presentation.common



import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@Composable
private fun Etiqueta(texto: String) {
    Text(text = texto, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
}

@Composable
private fun MensajeErrorCampo(mensaje: String?) {
    if (mensaje != null) {
        Text(text = mensaje, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
    }
}

/** Selector de una opción entre varias (menú desplegable anclado a un botón). */
@Composable
fun SelectorDesplegable(
    etiqueta: String,
    seleccionado: String?,
    opciones: List<String>,
    onSeleccion: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    mensajeError: String? = null,
) {
    var expandido by remember { mutableStateOf(false) }
    Column(modifier = modifier.fillMaxWidth()) {
        Etiqueta(etiqueta)
        Box {
            OutlinedButton(
                onClick = { expandido = true },
                enabled = enabled,
                modifier = Modifier.fillMaxWidth(),
            ) { Text(seleccionado ?: "Elegir…") }
            DropdownMenu(expanded = expandido, onDismissRequest = { expandido = false }) {
                opciones.forEach { opcion ->
                    DropdownMenuItem(
                        text = { Text(opcion) },
                        onClick = {
                            expandido = false
                            onSeleccion(opcion)
                        },
                    )
                }
            }
        }
        MensajeErrorCampo(mensajeError)
    }
}

/** Selector de fecha. Entrega el texto "AAAA-MM-DD" que espera el backend. */
@Composable
fun SelectorFecha(
    etiqueta: String,
    fecha: String?,
    onFechaSeleccionada: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    mensajeError: String? = null,
) {
    var mostrar by remember { mutableStateOf(false) }
    Column(modifier = modifier.fillMaxWidth()) {
        Etiqueta(etiqueta)
        OutlinedButton(onClick = { mostrar = true }, enabled = enabled, modifier = Modifier.fillMaxWidth()) {
            Text(fecha ?: "Elegir fecha")
        }
        MensajeErrorCampo(mensajeError)
    }
    if (mostrar) {
        val estado = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { mostrar = false },
            confirmButton = {
                TextButton(onClick = {
                    estado.selectedDateMillis?.let { onFechaSeleccionada(formatearFechaUtc(it)) }
                    mostrar = false
                }) { Text("Aceptar") }
            },
            dismissButton = { TextButton(onClick = { mostrar = false }) { Text("Cancelar") } },
        ) { DatePicker(state = estado) }
    }
}

/** Selector de hora en formato 24 h. Entrega el texto "HH:MM". */
@Composable
fun SelectorHora(
    etiqueta: String,
    hora: String?,
    onHoraSeleccionada: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    mensajeError: String? = null,
) {
    var mostrar by remember { mutableStateOf(false) }
    Column(modifier = modifier.fillMaxWidth()) {
        Etiqueta(etiqueta)
        OutlinedButton(onClick = { mostrar = true }, enabled = enabled, modifier = Modifier.fillMaxWidth()) {
            Text(hora ?: "Elegir hora")
        }
        MensajeErrorCampo(mensajeError)
    }
    if (mostrar) {
        val estado = rememberTimePickerState(initialHour = 15, initialMinute = 0, is24Hour = true)
        AlertDialog(
            onDismissRequest = { mostrar = false },
            confirmButton = {
                TextButton(onClick = {
                    onHoraSeleccionada(String.format(Locale.US, "%02d:%02d", estado.hour, estado.minute))
                    mostrar = false
                }) { Text("Aceptar") }
            },
            dismissButton = { TextButton(onClick = { mostrar = false }) { Text("Cancelar") } },
            text = { TimePicker(state = estado) },
        )
    }
}

/** El DatePicker entrega medianoche UTC: se formatea en UTC para que no se corra un día. */
private fun formatearFechaUtc(milisegundos: Long): String =
    SimpleDateFormat("yyyy-MM-dd", Locale.US)
        .apply { timeZone = TimeZone.getTimeZone("UTC") }
        .format(Date(milisegundos))