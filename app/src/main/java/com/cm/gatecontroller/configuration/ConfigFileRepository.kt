package com.cm.gatecontroller.configuration

import android.net.Uri
import com.cm.gatecontroller.configuration.model.ConfigData

interface ConfigFileRepository {
    suspend fun saveConfiguration(uri: Uri, configData: ConfigData): Result<Unit>
    suspend fun loadConfiguration(uri: Uri): Result<Map<String, String>>
}
