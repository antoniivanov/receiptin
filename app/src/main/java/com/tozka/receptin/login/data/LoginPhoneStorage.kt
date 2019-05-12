package com.tozka.receptin.login.data

import android.content.Context
import com.tozka.receptin.R
import com.tozka.receptin.login.data.model.LoggedInUser

class LoginPhoneStorage(private val context: Context) {

    private val preferenceUserKey = context.getString(R.string.preference_user_key)
    private val preferencePasswordKey = context.getString(R.string.preference_password_key)

    private var pref = context.getSharedPreferences(
        context.getString(R.string.preference_file_key),
        Context.MODE_PRIVATE
    )

    fun storeCredentials(user: LoggedInUser) {
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore

        // TODO: do not user shared preferences to keep credentials

        with(pref.edit()) {
            putString(preferenceUserKey, user.userId)
            putString(preferencePasswordKey, user.password)
        }.commit()
    }

    fun fetchCredentials(): LoggedInUser? {
        var userId = pref.getString(preferenceUserKey, null)
        var password = pref.getString(preferencePasswordKey, null)
        if (userId != null && password != null) {
            return LoggedInUser(userId = userId, password = password, displayName = userId)
        } else {
            return null
        }
    }
}