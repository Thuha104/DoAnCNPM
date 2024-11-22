package com.example.stopwatch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.stopwatch.ui.theme.StopWatchTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StopWatchTheme {
                ScreenStopWatch()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenStopWatch() {
    var timeDisplay by remember { mutableStateOf("00:00:00:00") }
    var isRunning by remember { mutableStateOf(false) }
    var elapsedTime by remember { mutableStateOf(0L) }
    var lapTime by remember { mutableStateOf(0L) } // New variable for lap time
    val coroutineScope = rememberCoroutineScope()
    var lapTimes by remember { mutableStateOf(listOf<Pair<String, Color>>()) }

    // Trạng thái cho các nút
    val isLapEnabled by remember { derivedStateOf { isRunning } }
    val isPauseEnabled by remember { derivedStateOf { isRunning } }
    val isResetEnabled by remember { derivedStateOf { !isRunning } }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Stop Watch", fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(9.dp))
                        Icon(imageVector = Icons.Default.Favorite, contentDescription = "Clock Icon")
                    }
                },
                actions = {
                    // Hiển thị tấm ảnh ở góc phải
                    Image(
                        painter = painterResource(id = R.drawable.logo), // Tên ảnh của bạn
                        contentDescription = "Icon Image",
                        modifier = Modifier
                            .size(100.dp) // Kích thước của ảnh
                            .padding(end = 8.dp) // Khoảng cách với cạnh phải
                    )
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color.Black
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(20.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(230.dp)
                    .border(2.dp, color = Color.Black, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                BasicTextField(
                    value = timeDisplay,
                    onValueChange = {},
                    textStyle = TextStyle(fontSize = 40.sp),
                    modifier = Modifier.fillMaxWidth().padding(10.dp),
                    enabled = false,
                )
            }

            Spacer(modifier = Modifier.height(25.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = {
                        if (!isRunning) {
                            isRunning = true
                            coroutineScope.launch {
                                while (isRunning) {
                                    delay(10L)
                                    elapsedTime += 10L
                                    lapTime += 10L // Increase lap time as well
                                    timeDisplay = formatTime(elapsedTime)
                                }
                            }
                        }
                    }
                ) { Text(text = "Start") }

                Button(
                    onClick = {
                        isRunning = false
                    },
                    enabled = isPauseEnabled
                ) { Text(text = "Pause") }

                Button(
                    onClick = {
                        isRunning = false
                        elapsedTime = 0L
                        lapTime = 0L // Reset lap time
                        timeDisplay = formatTime(elapsedTime)
                        lapTimes = emptyList() // Xóa danh sách laps khi reset
                    },
                    enabled = isResetEnabled
                ) { Text(text = "Reset") }

                Button(
                    onClick = {
                        // Thêm thời gian hiện tại vào danh sách laps với màu ngẫu nhiên
                        val lapColor = getRandomColor()
                        lapTimes = lapTimes + Pair(formatTime(lapTime), lapColor)
                        lapTime = 0L // Reset lap time to 0 after recording
                    },
                    enabled = isLapEnabled
                ) { Text(text = "Laps") }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Sử dụng LazyColumn để hiển thị danh sách thời gian Laps
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp) // Khoảng cách giữa các mục
            ) {
                items(lapTimes) { lap ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(BorderStroke(2.dp, Color.Black), RoundedCornerShape(16.dp))
                            .padding(10.dp)
                    ) {
                        Text(
                            text = "Lap ${lapTimes.indexOf(lap) + 1}: ${lap.first}",
                            fontSize = 20.sp,
                            modifier = Modifier.fillMaxWidth(),
                            color = lap.second // Sử dụng màu ngẫu nhiên
                        )
                    }
                }
            }
        }
    }
}

// Hàm định dạng thời gian
fun formatTime(timeInMillis: Long): String {
    val hours = TimeUnit.MILLISECONDS.toHours(timeInMillis)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(timeInMillis) % 60
    val seconds = TimeUnit.MILLISECONDS.toSeconds(timeInMillis) % 60
    val milliseconds = (timeInMillis % 1000) / 10
    return String.format("%02d:%02d:%02d:%02d", hours, minutes, seconds, milliseconds)
}

// Hàm lấy màu ngẫu nhiên
fun getRandomColor(): Color {
    return Color(Random.nextFloat(), Random.nextFloat(), Random.nextFloat())
}
