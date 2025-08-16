package app.morning.glory.ui.home.components.sheets

import android.content.SharedPreferences
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import app.morning.glory.core.utils.AppPreferences
import app.morning.glory.ui.qr_scanner.ScannerActivity
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QRCodeManagerSheetBody() {
    var alarmCode by remember {mutableStateOf(AppPreferences.alarmCode)}

    DisposableEffect(Unit) {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == AppPreferences.ALARM_CODE_KEY) {
                alarmCode = AppPreferences.alarmCode
            }
        }
        AppPreferences.registerListener(listener)

        onDispose {
            AppPreferences.unregisterListener(listener)
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Saved QR Codes",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "The alarm QR code makes dismissing the alarm harder. Place it somewhere that forces you to get out of bed to scan it.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(36.dp))

        AddNewQRCode()

        Spacer(modifier = Modifier.height(24.dp))

        if (alarmCode != null ) {
            DeleteExistingCodeButton(alarmCode!!)

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun DeleteExistingCodeButton(qrCode: String) {
    Button(
        onClick = { AppPreferences.alarmCode = null },
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        ),
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(8.dp)
        ) {
            Text(
                text = "Delete Existing Code",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
            Text(
                text = qrCode,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
        }
    }

}

@Composable
fun AddNewQRCode() {
    val barcodeLauncher = rememberLauncherForActivityResult(
        contract = ScanContract()
    ) { result ->
        if (result.contents == null) {
            Log.d("QRScannerScreen", "Scan cancelled")
        } else {
            AppPreferences.alarmCode =  result.contents
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
            .fillMaxWidth()
            .height(48.dp)
    ) {
        Text(text = "Scan QR Code")
    }
}
