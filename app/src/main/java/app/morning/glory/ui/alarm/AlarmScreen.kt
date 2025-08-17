package app.morning.glory.ui.alarm

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.morning.glory.R
import app.morning.glory.core.extensions.toast
import app.morning.glory.core.utils.AppPreferences
import app.morning.glory.ui.qr_scanner.ScannerActivity
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AlarmScreen(
    onDismiss: () -> Unit = {},
    onSnooze: () -> Unit,
    snoozeCount: Int
) {

    val timeFormat = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
    val currentTime = timeFormat.format(Date())
    val alarmCode by remember { mutableStateOf(AppPreferences.alarmCode) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF1F2937),
                        Color(0xFF374151)
                    )
                )
            )
            .padding(24.dp)
    ) {
        // Animation rings
        AnimationRings()

        // Main content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.weight(1f))

            AlarmIndicator()

            Spacer(modifier = Modifier.height(32.dp))

            // Time display
            Text(
                text = currentTime,
                fontSize = 120.sp,
                color = Color.White,
                letterSpacing = (-2).sp
            )

            Spacer(modifier = Modifier.weight(1f))

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (alarmCode != null) {
                    ScanQRButton(alarmCode!!, onDismiss)
                } else {
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier
                            .height(56.dp)
                    ) {
                        Icon (
                            painter = painterResource(id = R.drawable.round_cancel_24),
                            contentDescription = "Dismiss alarm icon",
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Dismiss", fontSize = 18.sp)
                    }
                }

                if (snoozeCount < AppPreferences.maxSnoozeCount) {
                    Button(
                        onClick = onSnooze,
                        modifier = Modifier
                            .height(56.dp)
                    ) {
                        Icon (
                            painter = painterResource(id = R.drawable.outline_snooze_24),
                            contentDescription = "Snooze alarm icon",
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Snooze", fontSize = 18.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun ScanQRButton(alarmCode : String, onDismiss: () -> Unit) {
    val context = LocalContext.current
    val barcodeLauncher = rememberLauncherForActivityResult(
        contract = ScanContract()
    ) { result ->
        if (result.contents == null) {
            Log.d("QRScannerScreen", "Scan cancelled")
        } else {
            if (alarmCode == result.contents) {
                onDismiss()
            } else {
                Log.d("QRScannerScreen", "QR Code did not match: ${result.contents}")
                context.toast("QR Code did not match the alarm code.")
            }
        }
    }

    Button(
        onClick = {
            val options : ScanOptions = ScanOptions().apply {
                setDesiredBarcodeFormats(ScanOptions.ALL_CODE_TYPES)
                setPrompt("Scan a QR Code")
                setCaptureActivity(ScannerActivity::class.java)
                setBeepEnabled(true)
            }
            barcodeLauncher.launch(options)
        },
        modifier = Modifier
            .height(56.dp)
    ) {
        Icon (
            painter = painterResource(id = R.drawable.outline_qr_code_2_24),
            contentDescription = "QR Code Icon",
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text("Scan to Dismiss", fontSize = 18.sp)
    }
}

@Composable
fun AnimationRings() {
    val infiniteTransition = rememberInfiniteTransition(label = "rings")

    listOf(200.dp, 160.dp, 120.dp).forEachIndexed { index, size ->
        val scale by infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = 1.5f,
            animationSpec = infiniteRepeatable(
                animation = tween(2000, delayMillis = index * 500),
                repeatMode = RepeatMode.Restart
            ),
            label = "ring_scale_$index"
        )

        val alpha by infiniteTransition.animateFloat(
            initialValue = 0.3f,
            targetValue = 0f,
            animationSpec = infiniteRepeatable(
                animation = tween(2000, delayMillis = index * 500),
                repeatMode = RepeatMode.Restart
            ),
            label = "ring_alpha_$index"
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .wrapContentSize(Alignment.Center)
        ) {
            Box(
                modifier = Modifier
                    .size(size)
                    .scale(scale)
                    .alpha(alpha)
                    .border(
                        width = 1.dp,
                        color = Color.White,
                        shape = CircleShape
                    )
            )
        }
    }
}

@Composable
fun AlarmIndicator() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(R.drawable.round_alarm_24),
            contentDescription = "Alarm Active",
            tint = Color.White,
            modifier = Modifier
                .size(32.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = "Morning Glory",
            fontSize = 18.sp,
            color = Color.White,
            fontWeight = FontWeight.Medium,
        )
    }
}
