package com.example.grocerly.viewmodel

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.grocerly.Repository.FavouritesRepoImpl
import com.example.grocerly.model.FavouriteItem
import com.example.grocerly.model.Product
import com.example.grocerly.utils.NetworkResult
import com.example.grocerly.utils.NetworkUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
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
class FavouriteViewModel @Inject constructor(private val favouritesRepoImpl: FavouritesRepoImpl,application: Application): AndroidViewModel(application) {


    private val _favouritesState = MutableSharedFlow<NetworkResult<FavouriteItem>>()
    val favouritesState: SharedFlow<NetworkResult<FavouriteItem>> get() = _favouritesState.asSharedFlow()

    private val _favouritesList = MutableSharedFlow<NetworkResult<List<FavouriteItem>>>()
    val favouritesList: SharedFlow<NetworkResult<List<FavouriteItem>>> get() = _favouritesList.asSharedFlow()

    private val _deletedFavourite = MutableSharedFlow<NetworkResult<String>>()
    val deletedFavourite: SharedFlow<NetworkResult<String>> get() = _deletedFavourite.asSharedFlow()

    fun addToFavourites(favouriteItem: FavouriteItem){
        viewModelScope.launch {
            _favouritesState.emit(NetworkResult.Loading())
            val result = favouritesRepoImpl.addToFavouritesFirebase(favouriteItem)
            _favouritesState.emit(result)
        }
    }

    fun getAllFavouritesFromFirebase(){
        viewModelScope.launch {
            _favouritesList.emit(NetworkResult.Loading())
            favouritesRepoImpl.fetchAllFavourites().collectLatest { result ->
                _favouritesList.emit(result)
            }
        }
    }

    fun deleteFavouriteFromFirebase(favouriteItem: FavouriteItem){
        viewModelScope.launch {
            if (NetworkUtils.isNetworkAvailable(getApplication())){
                _deletedFavourite.emit(NetworkResult.Loading())
                val result = favouritesRepoImpl.deleteFavourite(favouriteItem)
                _deletedFavourite.emit(result)
            }else{
                _deletedFavourite.emit(NetworkResult.Error("Enable Wifi or Mobile data"))
            }
        }
    }

}