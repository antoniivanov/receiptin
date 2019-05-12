package com.tozka.receptin.login.data

import android.accounts.AccountManager
import com.tozka.receptin.login.data.model.LoggedInUser
import java.io.IOException
import java.io.Serializable

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
interface LoginDataSource {

    fun login(username: String, password: String): Result<LoggedInUser>

    fun logout()
}

interface LoginDataSourceFactory : Serializable {
    fun newDataSource() : LoginDataSource
}

class DefaultLoginDataSource : LoginDataSource {
    override fun login(username: String, password: String): Result<LoggedInUser> {
        try {
            val fakeUser = LoggedInUser(username, username, password)

            return Result.Success(fakeUser)
        } catch (e: Throwable) {
            return Result.Error(IOException("Error logging in", e))
        }
    }

    override fun logout() {
        // TODO: revoke authentication
    }
}