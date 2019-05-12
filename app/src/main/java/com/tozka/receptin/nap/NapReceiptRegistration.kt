package com.tozka.receptin.nap

import com.tozka.receptin.ReceiptRegistration
import com.tozka.receptin.login.data.Result
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.Serializable
import java.lang.RuntimeException
import java.text.DecimalFormat
import java.text.SimpleDateFormat


const val NAP_MAIN_URL = "https://kasovbon.bg"
const val NAP_LOGIN_URL = "https://kasovbon.bg/login"
const val NAP_REGISTER_RECEIPT_URL = "https://kasovbon.bg/receipt"

open class RegistrationException(message : String) : RuntimeException(message) {}

class LoginRegistrationException(message : String)  : RegistrationException(message) {}
class FailedRegistrationException(message : String)  : RegistrationException(message) {}

class NapReceiptRegistration(var username : String, var password : String) : ReceiptRegistration, Serializable {

   // private val log = LoggerFactory.getLogger(javaClass)

    private var client = newHttpClient()

    private var isLoggedIn = false

    override fun register(receipt: Receipt) {
        //log.debug("receipt is $receipt")
        if (!isLoggedIn) {
            login()
        }
        if(!registerReceipt(receipt)) {
            //log.debug("Try logging again in case of auth issue")
            login()
            if (!registerReceipt(receipt)) {
                throw FailedRegistrationException("Failed to register receipt ")
            }
        }
    }

    fun newHttpClient(): OkHttpClient {
        return OkHttpClient.Builder().apply {
            cookieJar(PersistentCookieStore())
        }.build()
    }

    fun login() {
        //TODO: when the cookie expires or failed to authenticate ?
        //TODO: https://square.github.io/retrofit/
        // re-create client on each login
        client = newHttpClient()
        val loginDataSource = NapLoginDataSource(client)
        val res = loginDataSource.login(username, password)
        if (res is Result.Error) {
            throw res.exception
        }
        isLoggedIn = true
    }

    private fun registerReceipt(receipt: Receipt): Boolean {
        var bon_date_formatter = SimpleDateFormat("dd.MM.yyyy")
        var bon_hour_minute_formatter = SimpleDateFormat("HH:mm")
        var bon_value_formatter = DecimalFormat("#.00");


        var formBody = MultipartBody.Builder().apply {
            setType(MultipartBody.FORM)
            addFormDataPart("bon_date", bon_date_formatter.format(receipt.date))
            addFormDataPart("bon_hour_minutes", bon_hour_minute_formatter.format(receipt.date))
            addFormDataPart("bon_value", bon_value_formatter.format(receipt.amount))

            var posHourMinsString =
                if (receipt.pos_date != null) bon_hour_minute_formatter.format(receipt.pos_date) else ""
            addFormDataPart("pos_hour_minutes", posHourMinsString)
        }.build()

        var request = Request.Builder().apply {
            url(NAP_REGISTER_RECEIPT_URL)
            post(formBody)
        }.build()

        var response = client.newCall(request).execute()
//        println("response is $response, headers: ${response.headers()}  body : ${response.body()?.string()} status: ${response.code()}")

        ////alert-danger

        var isSuccessful = response.body()?.string()?.contains("Успешно регистрирахте касова бележка")
        return isSuccessful ?: false
    /*
        <div class="alert alert-danger" style="display:block;">
        Касовата бележка вече е въведена!										</div>

        <div class="alert alert-success" style="display:block;">
    Успешно регистрирахте касова бележка.<br />Пазете касовата си бележка до края на играта!
     */
    }
}
//
//fun main() {
//    var r = NapReceiptRegistration(username = "anthvt@yahoo.co.uk", password = "1123581321")
//    var d = GregorianCalendar().apply {
//        set(2019, 2, 3, 12, 32)
//    }.time
//    r.register(Receipt(d, 123.64, null))
//}