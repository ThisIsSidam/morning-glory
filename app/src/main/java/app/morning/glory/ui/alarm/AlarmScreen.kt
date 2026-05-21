package app.morning.glory.ui.alarm

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Snooze
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.morning.glory.R
import app.morning.glory.core.extensions.toast
import app.morning.glory.core.utils.AlarmType
import app.morning.glory.core.utils.AppPreferences
import app.morning.glory.ui.qr_scanner.ScannerActivity
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AlarmScreen(
    alarmType: AlarmType,
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
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.surface
                    )
                )
            )
            .padding(24.dp)
    ) {
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

            Spacer(modifier = Modifier.weight(0.5f))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 32.dp, vertical = 16.dp)
                    .animateContentSize()
            ) {
                if (alarmCode != null && alarmType == AlarmType.SLEEP) {
                    ScanQRButton(
                        alarmCode = alarmCode!!,
                        onDismiss = onDismiss,
                        modifier = Modifier
                            .height(60.dp)
                            .widthIn(min = 200.dp, max = 360.dp)
                            .fillMaxWidth(0.85f)
                    )
                } else {
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier
                            .height(60.dp)
                            .widthIn(min = 200.dp, max = 360.dp)
                            .fillMaxWidth(0.85f)
                            .clip(RoundedCornerShape(24.dp)),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.round_cancel_24),
                            contentDescription = "Stop alarm icon",
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Stop", fontSize = 18.sp)
                    }
                }

                if (snoozeCount < AppPreferences.maxSnoozeCount) {
                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = onSnooze,
                        modifier = Modifier
                            .height(60.dp)
                            .widthIn(min = 200.dp, max = 360.dp)
                            .fillMaxWidth(0.85f)
                            .clip(RoundedCornerShape(24.dp)),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Snooze,
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
fun ScanQRButton(alarmCode: String, modifier: Modifier = Modifier, onDismiss: () -> Unit) {
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
            val options: ScanOptions = ScanOptions().apply {
                setDesiredBarcodeFormats(ScanOptions.ALL_CODE_TYPES)
                setPrompt("Scan a QR Code")
                setCaptureActivity(ScannerActivity::class.java)
                setBeepEnabled(false)
            }
            barcodeLauncher.launch(options)
        },
        modifier = modifier.clip(RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.outline_qr_code_2_24),
            contentDescription = "QR Code Icon",
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text("Scan to Dismiss", fontSize = 18.sp)
    }
}

@Composable
fun AlarmIndicator() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(R.mipmap.ic_launcher_foreground),
            contentDescription = "Alarm Active",
            tint = Color.White,
            modifier = Modifier.size(32.dp)
        )

        Spacer(modifier = Modifier.width(4.dp))

        Text(
            text = "Morning Glory",
            fontSize = 18.sp,
            color = Color.White,
            fontWeight = FontWeight.Medium,
        )
    }
}
