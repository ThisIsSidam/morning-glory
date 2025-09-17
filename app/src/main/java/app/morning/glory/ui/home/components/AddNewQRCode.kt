
import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import app.morning.glory.core.utils.AppPreferences
import app.morning.glory.ui.qr_scanner.ScannerActivity
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions

@Composable
fun AddNewQRCode() {
    val context = LocalContext.current
    var showPermissionDialog by remember { mutableStateOf(false) }

    val barcodeLauncher = rememberLauncherForActivityResult(
        contract = ScanContract()
    ) { result ->
        if (result.contents == null) {
            Log.d("QRScannerScreen", "Scan cancelled")
        } else {
            AppPreferences.alarmCode = result.contents
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // permission granted → launch scanner
            val options = ScanOptions().apply {
                setDesiredBarcodeFormats(ScanOptions.ALL_CODE_TYPES)
                setPrompt("Scan a QR Code")
                setCaptureActivity(ScannerActivity::class.java)
                setBeepEnabled(true)
            }
            barcodeLauncher.launch(options)
        } else {
            // permission denied
            showPermissionDialog = true
        }
    }

    Button(
        onClick = {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                // Already allowed → directly launch scanner
                val options = ScanOptions().apply {
                    setDesiredBarcodeFormats(ScanOptions.ALL_CODE_TYPES)
                    setPrompt("Scan a QR Code")
                    setCaptureActivity(ScannerActivity::class.java)
                    setBeepEnabled(true)
                }
                barcodeLauncher.launch(options)
            } else {
                // Ask for permission
                permissionLauncher.launch(Manifest.permission.CAMERA)
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
    ) {
        Text(text = "Scan QR Code")
    }

    if (showPermissionDialog) {
        AlertDialog(
            onDismissRequest = { /* block outside dismiss */ },
            title = { Text("Camera Permission Needed") },
            text = { Text("This feature requires access to the camera to scan QR codes.") },
            confirmButton = {
                TextButton(onClick = {
                    showPermissionDialog = false
                    val intent = Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.fromParts("package", context.packageName, null)
                    )
                    context.startActivity(intent)
                }) {
                    Text("Allow")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showPermissionDialog = false
                    if (context is Activity) context.finish()
                }) {
                    Text("Close")
                }
            }
        )
    }
}
