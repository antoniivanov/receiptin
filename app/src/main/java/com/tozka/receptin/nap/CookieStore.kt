package com.tozka.receptin.nap

import android.R.attr.path
import okhttp3.Cookie
import okhttp3.HttpUrl
import okhttp3.CookieJar


class CookieStore : CookieJar {

    private val cookieStore = HashSet<Cookie>()

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        /**
         * Saves cookies from HTTP response
         * If the response includes a trailer this method is called second time
         */
        //Save cookies to the store
        cookieStore.addAll(cookies)
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        /**
         * Load cookies from the jar for an HTTP request.
         * This method returns cookies that have not yet expired
         */
        val validCookies = ArrayList<Cookie>()
        for (cookie in cookieStore) {
            //LogCookie(cookie)
            if (cookie.expiresAt() < System.currentTimeMillis()) {
                // invalid cookies
            } else {
                validCookies.add(cookie)
            }
        }
        return validCookies
    }

    //Print the values of cookies - Useful for testing
    private fun LogCookie(cookie: Cookie) {
        println("String: $cookie")
        println("Expires: " + cookie.expiresAt())
        println("Hash: " + cookie.hashCode())
        println("Path: " + cookie.path())
        println("Domain: " + cookie.domain())
        println("Name: " + cookie.name())
        println("Value: " + cookie.value())
    }
}
