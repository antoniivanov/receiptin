package com.tozka.receptin.barcode

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import me.dm7.barcodescanner.zbar.Result
import me.dm7.barcodescanner.zbar.ZBarScannerView
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.slf4j.LoggerFactory

const val BARCODE_SCANNING_ACTIVITY_RESULT_HANDLER_KEY: String = "BARCODE_SCANNING_ACTIVITY_RESULT_HANDLER_KEY"

class BarcodeScanningActivity : AppCompatActivity(), ZBarScannerView.ResultHandler {

    private val log = LoggerFactory.getLogger(javaClass)

    /*
    * Scanner View that will create the layout for scanning a barcode.
    * If you want a custom layout above the scanner layout, then implement
    * the scanning code in a fragment and use the fragment inside the activity,
    * add callbacks to obtain result from the fragment
    * */
    private lateinit var scannerView: ZBarScannerView
    private lateinit var barcodeResultHandler: BarcodeResultHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        scannerView = ZBarScannerView(this)
        setContentView(scannerView)

        val barcodeResultHandlerFactory: BarcodeResultHandlerFactory =
            intent.extras.getSerializable(BARCODE_SCANNING_ACTIVITY_RESULT_HANDLER_KEY) as BarcodeResultHandlerFactory
        barcodeResultHandler = barcodeResultHandlerFactory.newHandler()
        log.info("Created ${javaClass.simpleName} with handler ${barcodeResultHandler.javaClass.simpleName}")
    }

    /*
    * It is required to start and stop camera in lifecycle methods
    * (onResume and onPause)
    * */
    override fun onResume() {
        super.onResume()
        //log.debug( "Resume camera")
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
        //Toast.makeText(this, result?.contents, Toast.LENGTH_SHORT).show()
        log.debug("Handle result : ${result?.contents} with format ${result?.barcodeFormat?.name}")
        doAsync {
            val isOK = barcodeResultHandler.handle(BarcodeResult(result?.contents ?: ""))

            uiThread {
                if (!isOK) { // TODO - this should be in MainActivity
                    val errorString = "Failed to register receipt"
                    Toast.makeText(applicationContext, errorString, Toast.LENGTH_SHORT).show()
                    Toast.makeText(applicationContext, errorString, Toast.LENGTH_SHORT).show()
                } else {
                    val message = "Succesffully registered receipt"
                    Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
                    Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        //Camera will stop after scanning result, so we need to resume the
        //preview in order scan more codes
        scannerView.resumeCameraPreview(this)
    }
}
