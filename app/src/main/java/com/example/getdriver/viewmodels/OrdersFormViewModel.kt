package com.example.getdriver.viewmodels

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.getdriver.data.local.model.Orders
import com.example.getdriver.data.repository.OrdersRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import okhttp3.MultipartBody
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class OrdersFormViewModel @Inject constructor(
    mainRepository: OrdersRepository
) : ViewModel() {

    private var repository = mainRepository

    private val _loginState = MutableStateFlow<LoginUIState>(LoginUIState.Empty)
    private val loginUIState: StateFlow<LoginUIState> = _loginState

    private val _isLoading: MutableState<Boolean> = mutableStateOf(false)
    private val isLoading: State<Boolean> get() = _isLoading

    //simulate login process
    suspend fun addOrder(from_addr: String, to_addr: String, cost: String, latitude: String, longitude: String): Flow<List<Orders>> {

        val sdf = SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss")
        val currentDate = sdf.format(Date())

        val orders = Orders(
            from_addr = from_addr,
            to_addr = to_addr,
            cost = cost,
            latitude = latitude,
            longitude = longitude,
            user_id = 1
        )

        return repository.addOrder(
            onStart = {_loginState.value = LoginUIState.Loading },
            onCompletion = { _loginState.value =LoginUIState.Success },
            onError = { _loginState.value =LoginUIState.Error(it) },
            orders
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
