package com.example.grocerly.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.grocerly.preferences.GrocerlyDataStore
import com.example.grocerly.utils.Constants.ACCOUNTS
import com.example.grocerly.utils.Constants.USERS
import com.example.grocerly.utils.FirebaseErrorMapper
import com.example.grocerly.utils.LoginRegisterFieldState
import com.example.grocerly.utils.NetworkResult
import com.example.grocerly.utils.RegisterValidation
import com.example.grocerly.utils.validateEmail
import com.example.grocerly.utils.validatePassword
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.SetOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val auth: FirebaseAuth, private val db: FirebaseFirestore,private val grocerlyDataStore: GrocerlyDataStore, application: Application): AndroidViewModel(application) {



    private val _loginstate = MutableSharedFlow<NetworkResult<FirebaseUser>>()
    val loginstate : Flow<NetworkResult<FirebaseUser>> get() = _loginstate.asSharedFlow()

    private var _validationState = Channel<LoginRegisterFieldState>()
    val validationState:Flow<LoginRegisterFieldState> get() = _validationState.receiveAsFlow()

    fun setLoginState(loginstate:Boolean){
        viewModelScope.launch {
            grocerlyDataStore.setLoginState(loginstate)
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

        try {
            _loginstate.emit(NetworkResult.Loading())

            val firebaseUser = auth.signInWithEmailAndPassword(email,password).await()
            val user = firebaseUser.user
            val userId = user?.uid.toString()
            val userEmail = user?.email
            Log.d("userEmailgot",userEmail.toString())

            val sessionToken = UUID.randomUUID().toString()


            if (user!=null){

                val accountSnap = db.collection(ACCOUNTS).document(userId).get().await()

                if (accountSnap.exists()){
                    db.collection(ACCOUNTS)
                        .document(userId)
                        .update("email",userEmail)
                        .await()
                }

                val sessionData = mapOf(
                    "sessionToken" to sessionToken
                )

                db.collection(USERS)
                    .document(userId)
                    .set(sessionData, SetOptions.merge())
                    .await()

                grocerlyDataStore.setSessionToken(sessionToken)
                setLoginState(true)
                _loginstate.emit(NetworkResult.Success(user))
            }else{
                _loginstate.emit(NetworkResult.Error("User Login Failed"))
            }

        }catch (e: Exception){
            _loginstate.emit(NetworkResult.Error(FirebaseErrorMapper.getUserMessage(e)))
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