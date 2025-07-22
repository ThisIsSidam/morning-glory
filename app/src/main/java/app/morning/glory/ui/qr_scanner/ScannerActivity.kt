package app.morning.glory.ui.qr_scanner

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import app.morning.glory.R
import com.journeyapps.barcodescanner.CaptureActivity
import com.journeyapps.barcodescanner.CaptureManager
import com.journeyapps.barcodescanner.DecoratedBarcodeView

class ScannerActivity : CaptureActivity() {

    private lateinit var  capture: CaptureManager
    private lateinit var barcodeScannerView: DecoratedBarcodeView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        barcodeScannerView = initializeContent()

        capture = CaptureManager(this, barcodeScannerView)
        capture.initializeFromIntent(intent, savedInstanceState)
        capture.decode()
    }

    override fun initializeContent() : DecoratedBarcodeView {
        setContentView(R.layout.capture_layout)
        return findViewById<View?>(R.id.zxing_barcode_scanner) as DecoratedBarcodeView
    }

    override fun onResume() {
        super.onResume()
        capture.onResume()
    }

    override fun onPause() {
        super.onPause()
        capture.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        capture.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        capture.onSaveInstanceState(outState)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        capture.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return barcodeScannerView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event)
    }
}
