package com.example.grocerly.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.grocerly.preferences.GrocerlyDataStore
import com.example.grocerly.utils.NetworkResult
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(application: Application,private val auth:FirebaseAuth) :AndroidViewModel(application) {

    private val _logoutstate = MutableSharedFlow<NetworkResult<String>>()
    val logoutstate:Flow<NetworkResult<String>> get() = _logoutstate.asSharedFlow()


    private val grocerlyDataStore = GrocerlyDataStore(application)


    fun LogOutUserFromFirebase(){

       viewModelScope.launch {

           _logoutstate.emit(NetworkResult.Loading())
           try {

              EnableLogout()
               if (auth.currentUser==null){
                   _logoutstate.emit(NetworkResult.Success("Logged Out"))
               }


           }catch (e:Exception){
               _logoutstate.emit(NetworkResult.Error(e.message))
           }

       }
    }

    private suspend fun EnableLogout() {
        withContext(Dispatchers.IO){
            auth.signOut()
            grocerlyDataStore.setLoginState(false)
        }
    }

}