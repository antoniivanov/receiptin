package com.tozka.receptin.barcode

import java.io.Serializable

data class BarcodeResult(val content : String)

interface BarcodeResultHandler {

    fun handle(result : BarcodeResult) : Boolean
}

interface  BarcodeResultHandlerFactory : Serializable {
    fun newHandler() : BarcodeResultHandler
}