package com.cm.gatecontroller.core.serial

import com.cm.gatecontroller.core.serial.model.GateControllerState
import com.cm.gatecontroller.core.serial.model.GateState
import com.cm.gatecontroller.core.serial.model.SwitchState
import com.cm.gatecontroller.core.serial.model.UsageState
import com.cm.gatecontroller.core.serial.parser.AtCommandParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SerialRepositoryImpl @Inject constructor(
    private val serialClient: SerialClient,
    private val parser: AtCommandParser
) : SerialRepository {

    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _deviceStatus = MutableStateFlow(GateControllerState())
    override val deviceStatus: StateFlow<GateControllerState> = _deviceStatus.asStateFlow()

    init {
        repositoryScope.launch {
            serialClient.responses.collect { response ->
                val newState = parser.parse(response, _deviceStatus.value)
                _deviceStatus.value = newState
            }
        }
    }

    override suspend fun requestVersion() {
        serialClient.sendCommand(Command.RequestVersion.value)
    }

    // TODO: ViewModel 의존성 제거
    override suspend fun refreshMonitoring() {
        serialClient.sendCommand(Command.RequestVersion.value)
        serialClient.sendCommand(Command.RequestGate.value)
        serialClient.sendCommand(Command.RequestLamp.value)
        serialClient.sendCommand(Command.RequestLed.value)
        serialClient.sendCommand(Command.RequestRelay1.value)
        serialClient.sendCommand(Command.RequestRelay2.value)
        serialClient.sendCommand(Command.RequestPhoto1.value)
        serialClient.sendCommand(Command.RequestPhoto2.value)
        serialClient.sendCommand(Command.RequestOpen1.value)
        serialClient.sendCommand(Command.RequestOpen2.value)
        serialClient.sendCommand(Command.RequestOpen3.value)
        serialClient.sendCommand(Command.RequestOpen.value)
        serialClient.sendCommand(Command.RequestClose1.value)
        serialClient.sendCommand(Command.RequestClose2.value)
        serialClient.sendCommand(Command.RequestClose3.value)
        serialClient.sendCommand(Command.RequestClose.value)
        serialClient.sendCommand(Command.RequestLoopA.value)
        serialClient.sendCommand(Command.RequestLoopB.value)
        serialClient.sendCommand(Command.RequestMainPower.value)
        serialClient.sendCommand(Command.RequestTestCount.value)
        serialClient.sendCommand(Command.RequestDelayTime.value)
    }

    override suspend fun startTest() {
        serialClient.sendCommand(Command.StartTest.value)
    }

    override suspend fun stopTest() {
        serialClient.sendCommand(Command.StopTest.value)
    }

    // TODO: ViewModel 의존성 제거
    override suspend fun refreshConfiguration() {
        serialClient.sendCommand(Command.RequestVersion.value)
        serialClient.sendCommand(Command.RequestConfigLevelOpen.value)
        serialClient.sendCommand(Command.RequestConfigLevelClose.value)
        serialClient.sendCommand(Command.RequestConfigLamp.value)
        serialClient.sendCommand(Command.RequestConfigBuzzer.value)
        serialClient.sendCommand(Command.RequestConfigLampPosOn.value)
        serialClient.sendCommand(Command.RequestConfigLampPosOff.value)
        serialClient.sendCommand(Command.RequestConfigLedOpen.value)
        serialClient.sendCommand(Command.RequestConfigLedOpenPos.value)
        serialClient.sendCommand(Command.RequestConfigLedClose.value)
        serialClient.sendCommand(Command.RequestConfigLedClosePos.value)
        serialClient.sendCommand(Command.RequestConfigLoopA.value)
        serialClient.sendCommand(Command.RequestConfigLoopB.value)
        serialClient.sendCommand(Command.RequestConfigDelayTime.value)
        serialClient.sendCommand(Command.RequestConfigRelay1.value)
        serialClient.sendCommand(Command.RequestConfigRelay2.value)
    }

    override suspend fun setOpenLevel(level: Int) {
        serialClient.sendCommand(Command.SetOpenLevel(level).value)
    }

    override suspend fun setCloseLevel(level: Int) {
        serialClient.sendCommand(Command.SetCloseLevel(level).value)
    }

    override suspend fun setLampUsage(use: Boolean) {
        val state = if (use) UsageState.USE.name else UsageState.UNUSE.name
        serialClient.sendCommand(Command.SetLampUsage(state).value)
    }

    override suspend fun setBuzzerUsage(use: Boolean) {
        val state = if (use) UsageState.USE.name else UsageState.UNUSE.name
        serialClient.sendCommand(Command.SetBuzzerUsage(state).value)
    }

    override suspend fun setLampOnPosition(on: Boolean) {
        val state = if (on) GateState.OPENING.name else GateState.OPENED.name
        serialClient.sendCommand(Command.SetLampOnPosition(state).value)
    }

    override suspend fun setLampOffPosition(on: Boolean) {
        val state = if (on) GateState.CLOSING.name else GateState.CLOSED.name
        serialClient.sendCommand(Command.SetLampOffPosition(state).value)
    }

    override suspend fun setLedOpenColor(color: String) {
        serialClient.sendCommand(Command.SetLedOpenColor(color).value)
    }

    override suspend fun setLedOpenPosition(on: Boolean) {
        val state = if (on) GateState.OPENING.name else GateState.OPENED.name
        serialClient.sendCommand(Command.SetLedOpenPosition(state).value)
    }

    override suspend fun setLedCloseColor(color: String) {
        serialClient.sendCommand(Command.SetLedCloseColor(color).value)
    }

    override suspend fun setLedClosePosition(on: Boolean) {
        val state = if (on) GateState.CLOSING.name else GateState.CLOSED.name
        serialClient.sendCommand(Command.SetLedClosePosition(state).value)
    }

    override suspend fun setLoopAUsage(use: Boolean) {
        val state = if (use) UsageState.USE.name else UsageState.UNUSE.name
        serialClient.sendCommand(Command.SetLoopAUsage(state).value)
    }

    override suspend fun setLoopBUsage(use: Boolean) {
        val state = if (use) UsageState.USE.name else UsageState.UNUSE.name
        serialClient.sendCommand(Command.SetLoopBUsage(state).value)
    }

    override suspend fun setDelayTime(time: Int) {
        serialClient.sendCommand(Command.SetDelayTime(time).value)
    }

    override suspend fun setRelay1Mode(mode: Int) {
        serialClient.sendCommand(Command.SetRelay1Mode(mode).value)
    }

    override suspend fun setRelay2Mode(mode: Int) {
        serialClient.sendCommand(Command.SetRelay2Mode(mode).value)
    }

    override suspend fun factoryReset() {
        serialClient.sendCommand(Command.FactoryReset.value)
    }

    override suspend fun setControlLamp(on: Boolean) {
        val state = if (on) SwitchState.ON.name else SwitchState.OFF.name
        serialClient.sendCommand(Command.SetControlLamp(state).value)
    }

    override suspend fun setControlRelay1(on: Boolean) {
        val state = if (on) SwitchState.ON.name else SwitchState.OFF.name
        serialClient.sendCommand(Command.SetControlRelay1(state).value)
    }

    override suspend fun setControlRelay2(on: Boolean) {
        val state = if (on) SwitchState.ON.name else SwitchState.OFF.name
        serialClient.sendCommand(Command.SetControlRelay2(state).value)
    }

    override suspend fun setControlLed(color: String) {
        serialClient.sendCommand(Command.SetControlLed(color).value)
    }

    override suspend fun requestPosition() {
        serialClient.sendCommand(Command.RequestPosition.value)
    }

    override suspend fun openGateTest() {
        serialClient.sendCommand(Command.OpenGateTest.value)
    }

    override suspend fun closeGateTest() {
        serialClient.sendCommand(Command.CloseGateTest.value)
    }
}