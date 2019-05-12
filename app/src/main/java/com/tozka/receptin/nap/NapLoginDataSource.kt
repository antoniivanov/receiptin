package com.tozka.receptin.nap

import com.tozka.receptin.login.data.LoginDataSource
import com.tozka.receptin.login.data.Result
import com.tozka.receptin.login.data.model.LoggedInUser
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.slf4j.LoggerFactory

class NapLoginDataSource : LoginDataSource {

    var log = LoggerFactory.getLogger(javaClass)

    private var client: OkHttpClient

    constructor() {
        this.client = OkHttpClient.Builder().apply {
            cookieJar(PersistentCookieStore())
        }.build()
    }

    constructor(client : OkHttpClient) {
        this.client = client
    }

    override fun login(username: String, password: String): Result<LoggedInUser> {
        //TODO: when the cookie expires or failed to authenticate ?
        //TODO: https://square.github.io/retrofit/
        log.info("Login user $username")

        val cookieRequest = Request.Builder().apply { url(NAP_MAIN_URL) }.build()
        val cookieResponse = client.newCall(cookieRequest).execute()
        if (cookieResponse.header("Set-Cookie") == null) {
            return Result.Error(LoginRegistrationException("Failed to authenticate. Cannot set cookie"))
        }

        val loginFormBody = MultipartBody.Builder().apply {
            setType(MultipartBody.FORM)
            addFormDataPart("username", username)
            addFormDataPart("password", password)
        }.build()

        val loginRequest = Request.Builder().apply {
            url(NAP_LOGIN_URL)
            post(loginFormBody)
        }.build()

        val loginResponse = client.newCall(loginRequest).execute()
        val body = loginResponse?.body()?.string()

        if (body == null) {
            return Result.Error(LoginRegistrationException("Failed to login. Unknown error"))
        }
        if (body.contains("Паролата не съвпада с потребителското име") ||
            body.contains("Несъществуващ потребител")) {
            return Result.Error(LoginRegistrationException("Failed to login. Bad password or username"))
        }

        val fakeUser = LoggedInUser(username, username, password)
        return Result.Success(fakeUser)
    }

    override fun logout() {
    }
}