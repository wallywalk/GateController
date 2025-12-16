package com.cm.gatecontroller.core.serial.di

import com.cm.gatecontroller.core.serial.RealSerialClient
import com.cm.gatecontroller.core.serial.SerialClient
import com.cm.gatecontroller.core.serial.SerialRepository
import com.cm.gatecontroller.core.serial.SerialRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SerialModule {

    @Binds
    @Singleton
    abstract fun bindSerialClient(impl: RealSerialClient): SerialClient

    @Binds
    @Singleton
    abstract fun bindSerialRepository(impl: SerialRepositoryImpl): SerialRepository
}
