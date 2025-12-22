package com.cm.gatecontroller.configuration

import android.content.Context
import android.net.Uri
import com.cm.gatecontroller.configuration.model.ConfigData
import com.cm.gatecontroller.model.GateStatus
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject

class ConfigFileRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : ConfigFileRepository {

    override suspend fun saveConfiguration(uri: Uri, configData: ConfigData): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            context.contentResolver.openOutputStream(uri)?.bufferedWriter()?.use { writer ->
                val content = buildString {
                    appendLine("# Premium Gate Controller Config")
                    appendLine("VERSION=${configData.version}")
                    appendLine("LEVELOPEN=${configData.levelOpen}")
                    appendLine("LEVELCLOSE=${configData.levelClose}")
                    appendLine("LAMP=${configData.lamp.name}")
                    appendLine("BUZZER=${configData.buzzer.name}")
                    appendLine("LAMPON=${if (configData.lampPosOn == GateStatus.OPENING) "OPENING" else "OPENED"}")
                    appendLine("LAMPOFF=${if (configData.lampPosOff == GateStatus.CLOSING) "CLOSING" else "CLOSED"}")
                    appendLine("LEDOPEN=${configData.ledOpenColor.name}")
                    appendLine("LEDOPENPOS=${if (configData.ledOpenPos == GateStatus.OPENING) "OPENING" else "OPENED"}")
                    appendLine("LEDCLOSE=${configData.ledCloseColor.name}")
                    appendLine("LEDCLOSEPOS=${if (configData.ledClosePos == GateStatus.CLOSING) "CLOSING" else "CLOSED"}")
                    appendLine("LOOPA=${configData.loopA.name}")
                    appendLine("LOOPB=${configData.loopB.name}")
                    appendLine("DELAY=${configData.delayTime}")
                    appendLine("RELAY1=${configData.relay1}")
                    appendLine("RELAY2=${configData.relay2}")
                }
                writer.write(content)
            } ?: throw IOException("Failed to open output stream for URI: $uri")
        }
    }

    override suspend fun loadConfiguration(uri: Uri): Result<Map<String, String>> = withContext(Dispatchers.IO) {
        runCatching {
            val content = context.contentResolver.openInputStream(uri)?.bufferedReader()?.use { it.readText() }
                ?: throw IOException("Failed to open input stream for URI: $uri")
            parseConfigFile(content)
        }
    }

    private fun parseConfigFile(content: String): Map<String, String> {
        return content.lines()
            .asSequence()
            .map { it.trim() }
            .filter { it.isNotEmpty() && !it.startsWith("#") }
            .mapNotNull {
                val parts = it.split("=", limit = 2)
                if (parts.size == 2) parts[0].trim().uppercase() to parts[1].trim().uppercase() else null
            }
            .toMap()
    }
}
