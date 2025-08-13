        package com.example.grocerly.viewmodel

        import android.app.Application
        import android.util.Log
        import androidx.lifecycle.AndroidViewModel
        import androidx.lifecycle.LiveData
        import androidx.lifecycle.asLiveData
        import androidx.lifecycle.viewModelScope
        import com.example.grocerly.Repository.CartRepoImpl
        import com.example.grocerly.Repository.HomeRepoImpl
        import com.example.grocerly.model.CartProduct
        import com.example.grocerly.model.OfferItem
        import com.example.grocerly.model.ParentCategoryItem
        import com.example.grocerly.utils.NetworkResult
        import com.example.grocerly.utils.NetworkUtils
        import dagger.hilt.android.lifecycle.HiltViewModel
        import kotlinx.coroutines.Dispatchers
        import kotlinx.coroutines.cancel
        import kotlinx.coroutines.flow.Flow
        import kotlinx.coroutines.flow.MutableStateFlow
        import kotlinx.coroutines.flow.StateFlow
        import kotlinx.coroutines.flow.asStateFlow
        import kotlinx.coroutines.flow.cancel
        import kotlinx.coroutines.flow.collectLatest
        import kotlinx.coroutines.launch
        import javax.inject.Inject

        @HiltViewModel
        class HomeViewModel @Inject constructor (application: Application,private val homeRepoImpl: HomeRepoImpl,private val cartRepoImpl: CartRepoImpl):AndroidViewModel(application){

            private val _products = MutableStateFlow<NetworkResult<List<ParentCategoryItem>>>(NetworkResult.UnSpecified())
            val products: StateFlow<NetworkResult<List<ParentCategoryItem>>> get() = _products.asStateFlow()

            private val _offers = MutableStateFlow<NetworkResult<List<OfferItem>>>(NetworkResult.UnSpecified())
            val offers: StateFlow<NetworkResult<List<OfferItem>>> get() = _offers.asStateFlow()

            private val _cartItems = MutableStateFlow<NetworkResult<List<CartProduct>>>(NetworkResult.UnSpecified())
            val cartItems: StateFlow<NetworkResult<List<CartProduct>>> get() = _cartItems.asStateFlow()

            private val _homeAddress = MutableStateFlow<NetworkResult<String>>(NetworkResult.UnSpecified())
            val homeAddress: StateFlow<NetworkResult<String>> get() = _homeAddress.asStateFlow()

            init {
                fetchProductFromFirebase()
                fetchOffersFromFirebase()
                fetchCartItems()
                fetchHomeAddress()
            }

            fun fetchProductFromFirebase(){
                viewModelScope.launch {
                    _products.emit(NetworkResult.Loading())

                    homeRepoImpl.fetchProductFromFirebase().collectLatest {
                        _products.emit(it)

                    }}
            }


            fun fetchOffersFromFirebase(){
                viewModelScope.launch { handleNetworkResultOffersFetched() }
            }

            fun fetchCartItems(){
                viewModelScope.launch {
                    cartRepoImpl.fetchAllCartItems().collectLatest {
                        _cartItems.emit(it)
                    }
                }
            }

            fun fetchHomeAddress(){
                viewModelScope.launch {
                    getHomeAddress()
                }
            }

            private suspend fun handleNetworkResultOffersFetched() {
              if (NetworkUtils.isNetworkAvailable(getApplication())){
                  _offers.emit(NetworkResult.Loading())
                  val fetchedOffers = homeRepoImpl.getOffersFromFirebase()
                  _offers.emit(fetchedOffers)
              }else{
                  _offers.emit(NetworkResult.Error("Enable Wifi or Mobile data"))
              }
            }

            private suspend fun getHomeAddress(){
                val fetchedAddress = homeRepoImpl.getCityAndState()
                _homeAddress.emit(fetchedAddress)
            }

        }