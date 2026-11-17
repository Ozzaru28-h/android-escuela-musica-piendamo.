package com.example.escuela_msica_piendam.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.escuela_msica_piendam.domain.model.Rol
import com.example.escuela_msica_piendam.domain.model.Usuario
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

// A nivel de archivo: debe existir una sola instancia de DataStore por app.
private val Context.dataStoreSesion: DataStore<Preferences> by preferencesDataStore(name = "sesion")

/**
 * Guarda en el almacenamiento privado de la app el JWT y los datos mínimos de la sesión.
 * El AuthInterceptor lee de aquí el token en cada petición.
 */
@Singleton
class TokenStorage @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val claveToken = stringPreferencesKey("jwt_token")
    private val claveRol = stringPreferencesKey("rol")
    private val claveNombre = stringPreferencesKey("nombre")
    private val claveCategoria = stringPreferencesKey("categoria")

    /** Token guardado o null si no hay sesión. */
    suspend fun obtenerToken(): String? =
        context.dataStoreSesion.data.map { it[claveToken] }.first()

    /** Rol de la sesión guardada o null si no hay sesión. */
    suspend fun obtenerRol(): Rol? =
        context.dataStoreSesion.data.map { it[claveRol] }.first()
            ?.let { valor -> Rol.entries.firstOrNull { it.valorApi == valor } }

    /** Guarda token y datos del usuario en una sola operación atómica. */
    suspend fun guardarSesion(token: String, usuario: Usuario) {
        context.dataStoreSesion.edit { preferencias ->
            preferencias[claveToken] = token
            preferencias[claveRol] = usuario.rol.valorApi
            preferencias[claveNombre] = usuario.nombre
            val categoria = usuario.categoria
            if (categoria != null) {
                preferencias[claveCategoria] = categoria.valorApi
            } else {
                preferencias.remove(claveCategoria)
            }
        }
    }

    /** Cierra sesión: borra todo lo guardado. */
    suspend fun borrarSesion() {
        context.dataStoreSesion.edit { it.clear() }
    }
}
