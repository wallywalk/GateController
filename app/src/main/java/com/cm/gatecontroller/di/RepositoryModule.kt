package com.cm.gatecontroller.di

import com.cm.gatecontroller.auth.AuthRepository
import com.cm.gatecontroller.auth.AuthRepositoryImpl
import com.cm.gatecontroller.configuration.ConfigFileRepository
import com.cm.gatecontroller.configuration.ConfigFileRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindConfigFileRepository(impl: ConfigFileRepositoryImpl): ConfigFileRepository
}
