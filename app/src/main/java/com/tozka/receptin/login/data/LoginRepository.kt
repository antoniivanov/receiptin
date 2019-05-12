package com.tozka.receptin.login.data

import android.content.Context
import com.tozka.receptin.R
import com.tozka.receptin.login.data.model.LoggedInUser
import org.slf4j.LoggerFactory
import java.security.KeyStore

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */

class LoginRepository(val dataSource: LoginDataSource, val context: Context) {

    var log = LoggerFactory.getLogger(javaClass)

    // in-memory cache of the loggedInUser object
    var user: LoggedInUser? = null
        private set

    val isLoggedIn: Boolean
        get() = user != null

    init {
        user = null
    }

    fun logout() {
        user = null
        dataSource.logout()
    }

    fun login(username: String, password: String): Result<LoggedInUser> {
        // handle login
        val result = dataSource.login(username, password)
        log.info("Login result: $result")

        if (result is Result.Success) {
            setLoggedInUser(result.data)
        }

        return result
    }

    private fun setLoggedInUser(loggedInUser: LoggedInUser) {
        this.user = loggedInUser
        val storage = LoginPhoneStorage(context)
        storage.storeCredentials(loggedInUser)
    }
}
