package com.example.grocerly.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.grocerly.model.Account
import com.example.grocerly.utils.Constants.ACCOUNTS
import com.example.grocerly.utils.NetworkResult
import com.example.grocerly.utils.RegisterFieldState
import com.example.grocerly.utils.RegisterValidation
import com.example.grocerly.utils.validateEmail
import com.example.grocerly.utils.validateName
import com.example.grocerly.utils.validatePassword
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@HiltViewModel
class SignUpViewmodel @Inject constructor(application: Application,private val auth: FirebaseAuth,private val db: FirebaseFirestore):AndroidViewModel(application) {

    private var _isSigned = MutableStateFlow<NetworkResult<FirebaseUser>>(NetworkResult.UnSpecified())
    val isSigned: Flow<NetworkResult<FirebaseUser>> get() = _isSigned

    private var _validationState = Channel<RegisterFieldState>()
    var validationState:Flow<RegisterFieldState> = _validationState.receiveAsFlow()


    fun createUser(account: Account,password:String){
         viewModelScope.launch {
             if (validationChecker(account.firstName, account.email, password)) {
                 performUserSignUp(account, password)
             } else {
                 emitValidationErrors(account.firstName, account.email, password)
             }
         }
    }

    private suspend fun performUserSignUp(account: Account,password: String){
        _isSigned.value = NetworkResult.Loading()
        try {
            val firebaseResult = auth.createUserWithEmailAndPassword(account.email, password).await()
            val firebaseUser = firebaseResult.user

            val issaved = saveUserDetailsToFirebase(account)

            if (firebaseUser!=null && issaved) {
                _isSigned.value = NetworkResult.Success(firebaseUser)
            } else {
                _isSigned.value = NetworkResult.Error("User registration failed.")
            }
        } catch (e: Exception) {
            _isSigned.value = NetworkResult.Error(e.message ?: "An unknown error occurred.")
        }
    }

    private suspend fun saveUserDetailsToFirebase(account: Account): Boolean {
        val userId = auth.uid.toString()

        return suspendCoroutine {continuation->
            db.collection(ACCOUNTS)
                .document(userId)
                .set(account)
                .addOnSuccessListener{
                    continuation.resume(true)
                }
                .addOnFailureListener{
                    continuation.resume(false)
                }
        }
    }

    private fun validationChecker(name:String,email: String, password: String): Boolean {
        val isNameValidated = validateName(name)
        val isEmailValidated = validateEmail(email)
        val isPasswordValidated = validatePassword(password)
        val isValidated = isNameValidated is RegisterValidation.Success && isEmailValidated is RegisterValidation.Success && isPasswordValidated is RegisterValidation.Success
        return isValidated

    }

    private suspend fun emitValidationErrors(name: String, email: String, password: String) {
        val state = RegisterFieldState(
            validateName(name),validateEmail(email),validatePassword(password)
        )
        _validationState.send(state)
    }

}