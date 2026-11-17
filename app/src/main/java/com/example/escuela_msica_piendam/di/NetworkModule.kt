package com.example.escuela_msica_piendam.di



import com.example.escuela_msica_piendam.data.remote.api.ApiService
import com.example.escuela_msica_piendam.data.remote.interceptor.AuthInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/** Le enseña a Hilt cómo construir las piezas de red. Cada una se crea UNA sola vez (@Singleton). */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    // Pon aquí la IPv4 de tu computador (ej. 192.168.1.15). Debe terminar en "/".
    private const val BASE_URL = "https://backend-escuela-musica-piendamo.onrender.com/"
    private const val TIMEOUT_SEGUNDOS = 15L

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY // muestra peticiones y respuestas en Logcat
            redactHeader("Authorization")             // oculta el token en los logs
        }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor,
        loggingInterceptor: HttpLoggingInterceptor,
    ): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(authInterceptor)    // primero agrega el token...
            .addInterceptor(loggingInterceptor) // ...y después se registra la petición ya completa
            // Con timeouts cortos, un servidor inalcanzable produce el estado Error rápido
            // en vez de dejar la pantalla cargando por minutos.
            .connectTimeout(TIMEOUT_SEGUNDOS, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT_SEGUNDOS, TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT_SEGUNDOS, TimeUnit.SECONDS)
            .build()

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        check(!BASE_URL.contains("TU_DIRECCION_IPV4_AQUI")) {
            "Edita BASE_URL en NetworkModule.kt y escribe la IPv4 de tu computador"
        }
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create()) // JSON <-> objetos Kotlin
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService =
        retrofit.create(ApiService::class.java)
}