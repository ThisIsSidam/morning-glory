package app.morning.glory.ui.qr_scanner

import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.ImageButton
import app.morning.glory.R
import com.journeyapps.barcodescanner.CaptureActivity
import com.journeyapps.barcodescanner.CaptureManager
import com.journeyapps.barcodescanner.DecoratedBarcodeView

class ScannerActivity : CaptureActivity(), DecoratedBarcodeView.TorchListener {

    private lateinit var capture: CaptureManager
    private lateinit var barcodeScannerView: DecoratedBarcodeView
    private lateinit var switchFlashlightButton: ImageButton
    private var isFlashlightOn = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        barcodeScannerView = initializeContent()
        barcodeScannerView.setTorchListener(this)

        capture = CaptureManager(this, barcodeScannerView)
        capture.initializeFromIntent(intent, savedInstanceState)
        
        // Explicitly set the status text to override the library's default behavior
        barcodeScannerView.setStatusText(getString(R.string.scan_qr_code_description))

        capture.decode()

        switchFlashlightButton = findViewById(R.id.switch_flashlight)

        // if the device does not have flashlight in its camera,
        // then remove the switch flashlight button...
        if (!hasFlash()) {
            switchFlashlightButton.visibility = View.GONE
        }
    }

    override fun initializeContent(): DecoratedBarcodeView {
        setContentView(R.layout.capture_layout)
        return findViewById<View>(R.id.zxing_barcode_scanner) as DecoratedBarcodeView
    }

    /**
     * Toggle the flashlight
     */
    fun switchFlashlight(view: View) {
        if (isFlashlightOn) {
            barcodeScannerView.setTorchOff()
        } else {
            barcodeScannerView.setTorchOn()
        }
    }

    /**
     * Close the scanner activity
     */
    fun onCloseScanner(view: View) {
        finish()
    }

    private fun hasFlash(): Boolean {
        return applicationContext.packageManager
            .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)
    }

    override fun onTorchOn() {
        switchFlashlightButton.setBackgroundResource(R.drawable.button_bg_white_circle)
        switchFlashlightButton.setColorFilter(Color.BLACK)
        isFlashlightOn = true
    }

    override fun onTorchOff() {
        switchFlashlightButton.background = null
        switchFlashlightButton.colorFilter = null
        isFlashlightOn = false
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
