package com.example.grocerly.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.grocerly.preferences.GrocerlyDataStore
import com.example.grocerly.utils.LoginRegisterFieldState
import com.example.grocerly.utils.NetworkResult
import com.example.grocerly.utils.RegisterValidation
import com.example.grocerly.utils.validateEmail
import com.example.grocerly.utils.validatePassword
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val auth: FirebaseAuth, private val application: Application): AndroidViewModel(application) {

    private val _loginstate = MutableSharedFlow<NetworkResult<FirebaseUser>>()
    val loginstate : Flow<NetworkResult<FirebaseUser>> get() = _loginstate.asSharedFlow()

    private var _validationState = Channel<LoginRegisterFieldState>()
    val validationState:Flow<LoginRegisterFieldState> get() = _validationState.receiveAsFlow()

    val dataStore = GrocerlyDataStore(application)

    val getLoginState = dataStore.getLoginState().asLiveData(Dispatchers.IO)


    fun setLoginState(loginstate:Boolean){
        viewModelScope.launch {
            dataStore.setLoginState(loginstate)
        }
    }

    fun loginUserIntoFirebase(email: String,password: String){

        viewModelScope.launch{
            if (validationChecker(email,password)){
                performLoginUser(email,password)
            }else{
                emitValidationErrors(email,password)
            }
        }
    }



    private suspend fun performLoginUser(email: String,password: String){
        _loginstate.emit(NetworkResult.Loading())
        try {
            val firebaseUser = auth.signInWithEmailAndPassword(email,password).await()
            val user = firebaseUser.user

            if (user!=null){
                _loginstate.emit(NetworkResult.Success(user))
                setLoginState(true)
            }else{
                _loginstate.emit(NetworkResult.Error("User Login Failed"))
            }

        }catch (e: Exception){
            _loginstate.emit(NetworkResult.Error(e.message ?: "Unknown Error Occured"))
        }

    }



    private fun validationChecker(email: String, password: String): Boolean {
        val isEmailValidated = validateEmail(email)
        val isPasswordValidated = validatePassword(password)
        val isValidated = isEmailValidated is RegisterValidation.Success && isPasswordValidated is RegisterValidation.Success
        return isValidated

    }

    private suspend fun emitValidationErrors(email: String, password: String) {
        val state = LoginRegisterFieldState(
           validateEmail(email),validatePassword(password)
        )
        _validationState.send(state)
    }

}