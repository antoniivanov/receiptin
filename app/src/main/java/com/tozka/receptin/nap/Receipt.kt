package com.tozka.receptin.nap

import java.math.BigDecimal
import java.util.*

data class Receipt(val date : Date, val amount : Double, val pos_date : Date? = null)