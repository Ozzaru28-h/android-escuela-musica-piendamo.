package com.example.escuela_msica_piendam.domain.model

/**
 * Errores de la app expresados en lenguaje del usuario. Cada uno lleva un mensaje listo para
 * mostrarse en pantalla: la UI nunca ve excepciones técnicas (HttpException, IOException...).
 */
sealed class ErrorApp(mensaje: String) : Exception(mensaje) {

    /** El celular no llegó al servidor: sin internet, servidor apagado o URL equivocada. */
    class SinConexion :
        ErrorApp("No se pudo conectar con el servidor. Revisa tu conexión e inténtalo de nuevo.")

    /** HTTP 401: token ausente/vencido o credenciales incorrectas. */
    class NoAutorizado(mensaje: String) : ErrorApp(mensaje)

    /** El backend respondió con un error (400, 403, 404, 409, 500...) y explicó el motivo. */
    class Servidor(val codigo: Int, mensaje: String) : ErrorApp(mensaje)

    /** Cualquier otra cosa inesperada (por ejemplo, un JSON con una forma que la app no conoce). */
    class Desconocido : ErrorApp("Ocurrió un error inesperado. Inténtalo de nuevo.")
}
