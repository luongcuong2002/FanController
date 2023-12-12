package com.kma.fancontroller.viewmodel

import android.content.Context
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModel
import com.kma.fancontroller.ex.findActivity
import com.kma.fancontroller.model.FanLevelItem
import com.kma.fancontroller.utils.Constant
import com.kma.fancontroller.value.FanSpeedLevel

class HomeUiViewModel : ViewModel() {
    private val _isListening = mutableStateOf(false)

    val isListening: State<Boolean> = _isListening

    val fanSpeedLevels = mutableStateListOf<FanLevelItem>()
        .apply {
            FanSpeedLevel.values().forEach {
                add(FanLevelItem(it))
            }
        }

    fun startListening(context: Context) {
        setListening(true)
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        intent.putExtra(
            RecognizerIntent.EXTRA_CALLING_PACKAGE,
            context.packageName
        )
        intent.putExtra(
            RecognizerIntent.EXTRA_PROMPT,
            "Hãy nói lên mức độ gió bạn muốn"
        )
        intent.putExtra(
            RecognizerIntent.EXTRA_PARTIAL_RESULTS,
            true,
        )

        context.findActivity()?.let {
            ActivityCompat.startActivityForResult(it, intent, Constant.SPEECH_REQUEST_CODE, null)
        }
    }

    fun setListening(isListening: Boolean) {
        _isListening.value = isListening
    }

    fun selectLevelAndSendFanSpeedLevelToModule(fanSpeedLevel: FanSpeedLevel) {
        selectFanSpeedLevel(fanSpeedLevel)
    }

    fun selectFanSpeedLevel(fanSpeedLevel: FanSpeedLevel) {
        fanSpeedLevels.forEach { it.isSelected.value = false }
        fanSpeedLevels.firstOrNull { it.fanSpeedLevel == fanSpeedLevel }?.isSelected?.value = true
    }
}