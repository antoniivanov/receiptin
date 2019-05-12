package com.tozka.receptin

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.tozka.receptin.barcode.*
import com.tozka.receptin.login.data.LoginDataSource
import com.tozka.receptin.login.data.LoginDataSourceFactory
import com.tozka.receptin.login.data.LoginPhoneStorage
import com.tozka.receptin.login.data.model.LoggedInUser
import com.tozka.receptin.login.ui.login.LOGIN_ACTIVITY_DATA_SOURCE_KEY
import com.tozka.receptin.login.ui.login.LoginActivity
import com.tozka.receptin.nap.NapBarcodeResultHandler
import com.tozka.receptin.nap.NapLoginDataSource
import com.tozka.receptin.nap.NapReceiptRegistration

import kotlinx.android.synthetic.main.activity_main.*
import org.slf4j.LoggerFactory


class MainActivity : AppCompatActivity() {

    private val log = LoggerFactory.getLogger(javaClass)

    var currentUser : LoggedInUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        log.debug("Starting app $javaClass")

        checkForCurrentUserAndDisplay()

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 0)
        }

        loginButton.setOnClickListener {
            startLoginActivity()
        }

        scanBarcodeButton.setOnClickListener {
            startBarcodeActivity()
        }
    }

    private fun checkForCurrentUserAndDisplay() {
        // always re-fetch user in case it has been changed.
        val storage = LoginPhoneStorage(this)
        currentUser = storage.fetchCredentials()
        showOrHideLoginUser(currentUser)
    }

    override fun onResume() {
        super.onResume()
        checkForCurrentUserAndDisplay()
    }

    private fun showOrHideLoginUser(user: LoggedInUser?) {
        if (user?.userId != null) {
            val loginText = getString(R.string.login_user, user.userId)
            loginTextView.text = loginText
            loginTextView.visibility = View.VISIBLE
        } else {
            loginTextView.visibility = View.INVISIBLE
        }
    }

    private fun startBarcodeActivity() {
        val user = currentUser
        if (user == null) {
            Toast.makeText(this, getString(R.string.login_missing), Toast.LENGTH_LONG).show()
            Toast.makeText(this, getString(R.string.login_missing), Toast.LENGTH_LONG).show()
            return
        }
        val intent = Intent(this@MainActivity, BarcodeScanningActivity::class.java)

        val userId = user.userId
        val password =  user.password
        intent.putExtra(BARCODE_SCANNING_ACTIVITY_RESULT_HANDLER_KEY, object : BarcodeResultHandlerFactory {
            override fun newHandler(): BarcodeResultHandler {
                return NapBarcodeResultHandler(NapReceiptRegistration(userId, password))
            }
        })
        startActivity(intent)
    }

    private fun startLoginActivity() {
        val intent = Intent(this@MainActivity, LoginActivity::class.java)
        intent.putExtra(LOGIN_ACTIVITY_DATA_SOURCE_KEY, object : LoginDataSourceFactory {
            override fun newDataSource(): LoginDataSource {
                return NapLoginDataSource()
            }
        })
        startActivity(intent)
    }
}
