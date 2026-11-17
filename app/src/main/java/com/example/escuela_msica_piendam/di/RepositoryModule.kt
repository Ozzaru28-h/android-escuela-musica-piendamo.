package com.example.escuela_msica_piendam.di

import com.example.escuela_msica_piendam.data.repository.ActividadRepositoryImpl
import com.example.escuela_msica_piendam.data.repository.AuthRepositoryImpl
import com.example.escuela_msica_piendam.data.repository.InscripcionRepositoryImpl
import com.example.escuela_msica_piendam.domain.repository.ActividadRepository
import com.example.escuela_msica_piendam.domain.repository.AuthRepository
import com.example.escuela_msica_piendam.domain.repository.InscripcionRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds @Singleton
    abstract fun bindActividadRepository(impl: ActividadRepositoryImpl): ActividadRepository

    @Binds @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds @Singleton
    abstract fun bindInscripcionRepository(impl: InscripcionRepositoryImpl): InscripcionRepository
}