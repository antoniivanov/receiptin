package com.tozka.receptin

import com.tozka.receptin.nap.Receipt

interface ReceptImageScanner {

    fun imageToReceipt() : Receipt
}