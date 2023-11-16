package com.example.chatwithbranch.presentation.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatwithbranch.datamodels.LoginRequest
import com.example.chatwithbranch.datamodels.LoginResponse
import com.example.chatwithbranch.utils.AppResource
import com.priyanshub.branchchat.domain.usecase.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
): ViewModel() {

    private val _showProgress : MutableLiveData<Boolean> = MutableLiveData()
    val showProgress: LiveData<Boolean>
        get() = _showProgress

    private val _loginResponse : MutableLiveData<LoginResponse?> = MutableLiveData()
    val loginResponse: MutableLiveData<LoginResponse?>
        get() = _loginResponse

    fun login(username: String, password: String) = viewModelScope.launch {
        val loginRequest = LoginRequest(username, password)
        loginUseCase.invoke(loginRequest).collect{
            when(it){
                is AppResource.Loading -> {
                    _showProgress.postValue(true)
                }
                is AppResource.Error -> {
                    _loginResponse.postValue(null)
                    _showProgress.postValue(false)
                }
                is AppResource.Success -> {
                    _loginResponse.postValue(it.data?.body())
                    _showProgress.postValue(false)
                }
                else -> {}
            }
        }
    }

}