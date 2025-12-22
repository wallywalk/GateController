package com.cm.gatecontroller.debug

import androidx.lifecycle.ViewModel
import com.cm.gatecontroller.core.logger.DebugLogger
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DebugViewModel @Inject constructor(
    val logger: DebugLogger
) : ViewModel()