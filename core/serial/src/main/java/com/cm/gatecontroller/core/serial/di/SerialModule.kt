package com.cm.gatecontroller.core.serial.di

import android.content.Context
import com.cm.gatecontroller.core.serial.RealSerialClient
import com.cm.gatecontroller.core.serial.SerialClient
import com.cm.gatecontroller.core.serial.SerialRepository
import com.cm.gatecontroller.core.serial.SerialRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SerialRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindSerialRepository(impl: SerialRepositoryImpl): SerialRepository
}


@Module
@InstallIn(SingletonComponent::class)
object SerialClientModule {

    @Provides
    @Singleton
    fun provideRealSerialClient(@ApplicationContext context: Context): RealSerialClient {
        return RealSerialClient(context)
    }

    @Provides
    @Singleton
    fun provideSerialClient(realSerialClient: RealSerialClient): SerialClient {
        return realSerialClient
    }
}
