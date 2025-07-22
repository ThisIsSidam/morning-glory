package app.morning.glory.ui.home.components

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import app.morning.glory.R
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QRCodeManagerSheetBody() {
    val savedQRs = remember { mutableStateListOf<String>() }

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

        if (savedQRs.isNotEmpty()) {
            savedQRs.forEach { qrCode ->
                QRCodeTile(qrCode = qrCode)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        AddNewQRCode()
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun QRCodeTile(qrCode: String) {
    ListItem(
        headlineContent = { Text(qrCode) },
        trailingContent = { 
            IconButton(
                onClick = { /* Handle QR Code deletion */ }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.outline_delete_24),
                    contentDescription = "Delete QR Code Data"
                )
            }
        }
    )
}

@Composable
fun AddNewQRCode() {
    val barcodeLauncher = rememberLauncherForActivityResult(
        contract = ScanContract()
    ) { result ->
        if (result.contents == null) {
            Log.d("QRScannerScreen", "Scan cancelled")
        } else {
            Log.d("QRScannerScreen", "Scanned QR Code: ${result.contents}")
        }
    }

    Button(
        onClick = {
            val options : ScanOptions = ScanOptions().apply {
                setDesiredBarcodeFormats(ScanOptions.ALL_CODE_TYPES)
                setPrompt("Scan a QR Code")
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
