package com.tozka.receptin.login.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tozka.receptin.R
import com.tozka.receptin.login.data.LoginRepository
import com.tozka.receptin.login.data.Result
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread


class LoginViewModel(private val loginRepository: LoginRepository) : ViewModel() {

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    fun login(username: String, password: String) {
        // can be launched in a separate asynchronous job
        doAsync {
            val result = loginRepository.login(username, password)
            uiThread {
                if (result is Result.Success) {
                    _loginResult.value = LoginResult(success = LoggedInUserView(displayName = result.data.displayName))
                } else {
                    // TODO:??
                    _loginForm.value = LoginFormState(passwordError = R.string.login_failed)
                }
            }
        }
    }

    fun loginDataChanged(username: String, password: String) {
        if (!isUserNameValid(username)) {
            _loginForm.value = LoginFormState(usernameError = R.string.invalid_username)
        } else if (!isPasswordValid(password)) {
            _loginForm.value = LoginFormState(passwordError = R.string.invalid_password)
        } else {
            _loginForm.value = LoginFormState(isDataValid = true)
        }
    }

    private fun isUserNameValid(username: String): Boolean {
        return username.isNotBlank()
    }

    private fun isPasswordValid(password: String): Boolean {
        return password.isNotBlank()
    }
}
