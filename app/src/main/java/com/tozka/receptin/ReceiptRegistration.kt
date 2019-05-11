package com.tozka.receptin

import com.tozka.receptin.nap.Receipt

interface ReceiptRegistration {

    fun register(receipt: Receipt)
}