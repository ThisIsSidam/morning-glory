package app.morning.glory.ui.alarm

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
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

@Composable
fun AlarmScreen(
    onDismiss: () -> Unit = {},
    onSnooze: () -> Unit,
    snoozeCount: Int
) {
    val alarmCode by remember { mutableStateOf(AppPreferences.alarmCode) }

    Box(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .systemBarsPadding(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Time to Wake Up!",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            Row {
                if (alarmCode != null) {
                    ScanQRButton(alarmCode!!, onDismiss)
                } else {
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier
                            .height(56.dp)
                    ) {
                        Text("Dismiss", fontSize = 18.sp)
                    }
                }

                if (snoozeCount < 2) {
                    SnoozeButton(onSnooze)
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
            modifier = Modifier.padding(end = 8.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text("Scan to Dismiss", fontSize = 18.sp)
    }
}

@Composable
fun SnoozeButton(onSnooze: () -> Unit) {
    val context = LocalContext.current

    Button(
        onClick = {

        },
        modifier = Modifier
            .height(56.dp)
    ) {
        Icon (
            painter = painterResource(id = R.drawable.outline_snooze_24),
            contentDescription = "Snooze alarm icon",
            modifier = Modifier.padding(end = 8.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text("Snooze", fontSize = 18.sp)
    }
}