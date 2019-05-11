package com.tozka.receptin.barcode

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import me.dm7.barcodescanner.zbar.Result
import me.dm7.barcodescanner.zbar.ZBarScannerView

class BarcodeScanningActivity : AppCompatActivity(), ZBarScannerView.ResultHandler {

    private val tag : String = javaClass.simpleName

    /*
    * Scanner View that will create the layout for scanning a barcode.
    * If you want a custom layout above the scanner layout, then implement
    * the scanning code in a fragment and use the fragment inside the activity,
    * add callbacks to obtain result from the fragment
    * */
    private lateinit var scannerView: ZBarScannerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        scannerView = ZBarScannerView(this)
        setContentView(scannerView)
    }

    /*
    * It is required to start and stop camera in lifecycle methods
    * (onResume and onPause)
    * */
    override fun onResume() {
        super.onResume()
        Log.i(tag, "Resume camera")
        scannerView.setResultHandler(this)
        scannerView.startCamera()
     }

    override fun onPause() {
        super.onPause()
        scannerView.stopCamera()
    }

    override fun onStop() {
        super.onStop()
        scannerView.stopCamera()
    }

    override fun onDestroy() {
        super.onDestroy()
        scannerView.stopCamera()// Stop camera on pause
    }

    override fun handleResult(result: Result?) {
        Toast.makeText(this, result?.contents, Toast.LENGTH_SHORT).show()
        Log.i(tag, "this is result $result maybe ${result?.contents}")

        //Camera will stop after scanning result, so we need to resume the
        //preview in order scan more codes
        scannerView.resumeCameraPreview(this)
    }
}
