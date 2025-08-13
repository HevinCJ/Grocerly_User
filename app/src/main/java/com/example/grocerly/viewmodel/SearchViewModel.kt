package com.example.grocerly.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.grocerly.Repository.SearchRepoImpl
import com.example.grocerly.model.Product
import com.example.grocerly.utils.NetworkResult
import com.example.grocerly.utils.NetworkUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(private val searchRepoImpl: SearchRepoImpl,application: Application): AndroidViewModel(application) {

    private val _searchItem = MutableSharedFlow<NetworkResult<List<Product>>>()
    val searchItem: SharedFlow<NetworkResult<List<Product>>> get() = _searchItem.asSharedFlow()


    fun searchItemsInFirebase(query: String){
        viewModelScope.launch {
            if (NetworkUtils.isNetworkAvailable(getApplication())) {

                _searchItem.emit(NetworkResult.Loading())
                searchRepoImpl.searchProduct(query).collectLatest {
                    _searchItem.emit(it)
                    Log.d("searchItem",it.data.toString())
                }
            } else {
                _searchItem.emit(NetworkResult.Error("Enable Wifi or Mobile data"))
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }

}