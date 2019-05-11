package com.tozka.receptin

import android.Manifest
import android.accounts.Account
import android.accounts.AccountManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.tozka.receptin.barcode.BarcodeScanningActivity
import com.tozka.receptin.login.ui.login.LoginActivity

import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private val tag = javaClass.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        var accountManager = AccountManager.get(this)
        val accountType = javaClass.name
        var accounts = accountManager.getAccountsByType(accountType)
        var account : Account? = null;
        if (accounts.isEmpty()) {
            val intent = Intent(this@MainActivity, LoginActivity::class.java)
            startActivity(intent)

            // account = Account("anthvt@gmail.com", accountType)
            // accountManager.addAccountExplicitly(account, "1123581321", null)
        } else {
            account = accounts[0]
        }
        var password = accountManager.getPassword(account)
        Log.i(tag, "account is $account.name and password is $password")


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 0)
        }

        scanBarcodeButton.setOnClickListener {
            val intent = Intent(this@MainActivity, BarcodeScanningActivity::class.java)
            startActivity(intent)
        }

    }
}
