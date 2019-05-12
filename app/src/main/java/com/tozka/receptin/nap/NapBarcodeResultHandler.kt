package com.tozka.receptin.nap

import com.tozka.receptin.ReceiptRegistration
import com.tozka.receptin.barcode.BarcodeResult
import com.tozka.receptin.barcode.BarcodeResultHandler
import org.slf4j.LoggerFactory
import java.lang.Exception
import java.text.SimpleDateFormat

class NapBarcodeResultHandler(val registration : ReceiptRegistration) : BarcodeResultHandler {
    var log = LoggerFactory.getLogger(javaClass)

    override fun handle(result: BarcodeResult) : Boolean {
        // 44314497*0013878*2019-05-08*12:17:03*3.25
        var parts = result.content.split("*")
        if (parts.size != 5) {
            return false
        }

        val format = SimpleDateFormat("yyyy-MM-dd:HH:mm:ss")
        var receipt = Receipt(date = format.parse("${parts[2]}:${parts[3]}"), amount = parts[4].toDouble())

        try {
            registration.register(receipt)
            return true
        } catch (e : Exception) {
            // TODO: extract and display reason
            log.warn("Failed to register receipt - ${e.message}")
            return false
        }

    }
}