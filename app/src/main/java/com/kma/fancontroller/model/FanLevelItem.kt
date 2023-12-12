package com.kma.fancontroller.model

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.kma.fancontroller.value.FanSpeedLevel

data class FanLevelItem(
    val fanSpeedLevel: FanSpeedLevel,
    var isSelected: MutableState<Boolean> = mutableStateOf(false)
)
