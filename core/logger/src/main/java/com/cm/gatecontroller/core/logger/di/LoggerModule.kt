package com.cm.gatecontroller.core.logger.di

import com.cm.gatecontroller.core.logger.Logger
import com.cm.gatecontroller.core.logger.DebugLogger
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class LoggerModule {

    @Binds
    @Singleton
    abstract fun bindLogger(impl: DebugLogger): Logger
}
