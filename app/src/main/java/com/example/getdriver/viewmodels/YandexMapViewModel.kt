package com.example.getdriver.viewmodels

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.getdriver.data.local.model.YandexAddress
import com.example.getdriver.data.repository.OrdersRepository
import com.example.getdriver.data.repository.YandexMapRespository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class YandexMapViewModel @Inject constructor(
    private val yandexMapRespository: YandexMapRespository
): ViewModel(){

    private var repository = yandexMapRespository

    private val errorMessage = MutableLiveData<String>()

    private val _loginState = MutableStateFlow<LoginUIState>(LoginUIState.Empty)

    private val _isLoading: MutableState<Boolean> = mutableStateOf(false)


    fun getAddress(longlatStr: String): Flow<String> {
        return repository.getAddress(
            onStart = {_loginState.value = LoginUIState.Loading },
            onCompletion = { _loginState.value = LoginUIState.Success },
            onError = { _loginState.value = LoginUIState.Error(it) },
            longlatStr
        )
    }


    // login ui states
    sealed class LoginUIState {
        object Success : LoginUIState()
        data class Error(val message: String) : LoginUIState()
        object Loading : LoginUIState()
        object Empty : LoginUIState()
    }
}