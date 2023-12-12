package com.kma.fancontroller

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.toLowerCase
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kma.fancontroller.ui.theme.FanControllerTheme
import com.kma.fancontroller.utils.Constant
import com.kma.fancontroller.value.FanSpeedLevel
import com.kma.fancontroller.viewmodel.HomeUiViewModel


class HomeActivity : ComponentActivity() {

    private lateinit var homeUiViewModel: HomeUiViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        homeUiViewModel = ViewModelProvider(this).get(HomeUiViewModel::class.java)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
            != android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), 1)
        }

        setContent {
            FanControllerTheme {
                Screen()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constant.SPEECH_REQUEST_CODE) {
            homeUiViewModel.setListening(false)
            if (resultCode == RESULT_OK && data != null) {
                val results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                if (!results.isNullOrEmpty()) {
                    val recognizedText = results[0].trim().lowercase()
                    val fanSpeedLevel = FanSpeedLevel.values().firstOrNull {
                        it.recognitionValues.any { value ->
                            recognizedText.contains(value)
                        }
                    }
                    if (fanSpeedLevel != null) {
                        homeUiViewModel.selectLevelAndSendFanSpeedLevelToModule(fanSpeedLevel)
                    } else {
                        Toast.makeText(this, "Giá trị không hợp lệ!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}

@Composable
private fun Screen(homeUiViewModel: HomeUiViewModel = viewModel()) {

    val context = LocalContext.current

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            Text(
                text = "Điều khiển quạt",
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = Color.Black,
                modifier = Modifier
                    .padding(top = 30.dp)
            )

            LazyVerticalGrid(
                modifier = Modifier
                    .weight(1f)
                    .padding(20.dp),
                columns = GridCells.Adaptive(minSize = 128.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                horizontalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                items(FanSpeedLevel.values().size) { index ->

                    val item = homeUiViewModel.fanSpeedLevels[index]

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(20))
                            .background(
                                if (item.isSelected.value) {
                                    colorResource(id = R.color.teal_200)
                                } else {
                                    Color.LightGray
                                }
                            )
                            .clickable {
                                homeUiViewModel.selectLevelAndSendFanSpeedLevelToModule(item.fanSpeedLevel)
                            }
                    ) {
                        Text(
                            text = FanSpeedLevel.values()[index].name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            modifier = Modifier
                                .align(Alignment.Center),
                            color = Color.Black
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .padding(bottom = 15.dp)
                    .padding(horizontal = 30.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(15))
                    .background(colorResource(id = R.color.blue))
                    .padding(vertical = 10.dp)
                    .clickable {
                        if (!homeUiViewModel.isListening.value) {
                            homeUiViewModel.startListening(context)
                        }
                    }
            ) {
                Text(
                    text = "Nhấn để nói",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier
                        .align(Alignment.Center)
                )
            }
        }
    }
}