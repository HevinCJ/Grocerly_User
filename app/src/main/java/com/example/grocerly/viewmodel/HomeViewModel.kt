    package com.example.grocerly.viewmodel

    import android.util.Log
    import androidx.lifecycle.ViewModel
    import androidx.lifecycle.viewModelScope
    import com.example.grocerly.model.ParentCategoryItem
    import com.example.grocerly.model.Product
    import com.example.grocerly.utils.Constants.PARTNERS
    import com.example.grocerly.utils.Constants.PRODUCTS
    import com.example.grocerly.utils.NetworkResult
    import com.google.firebase.auth.FirebaseAuth
    import com.google.firebase.firestore.FirebaseFirestore
    import dagger.hilt.android.lifecycle.HiltViewModel
    import kotlinx.coroutines.flow.Flow
    import kotlinx.coroutines.flow.MutableStateFlow
    import kotlinx.coroutines.flow.asStateFlow
    import kotlinx.coroutines.launch
    import kotlinx.coroutines.tasks.await
    import javax.inject.Inject

    @HiltViewModel
    class HomeViewModel @Inject constructor (private val auth:FirebaseAuth,private val db:FirebaseFirestore):ViewModel(){

        private val _products = MutableStateFlow<NetworkResult<List<ParentCategoryItem>>>(NetworkResult.UnSpecified())
        val products: Flow<NetworkResult<List<ParentCategoryItem>>> get() = _products.asStateFlow()




        fun fetchProductFromFirebase(){
            viewModelScope.launch {

                _products.value = NetworkResult.Loading()

                try {
                    val querySnapshot = db.collectionGroup(PRODUCTS)
                        .get()
                        .await()

                    val groupedProducts = querySnapshot.toObjects(Product::class.java).groupBy {
                        it.category
                    }

                    val categories = groupedProducts.map { (category, products) ->
                        ParentCategoryItem(
                            categoryName = category.displayName,
                            childCategoryItems = products
                        )
                    }

                    val sortedCategories = categories.sortedBy { it.categoryName }

                    _products.value = NetworkResult.Success(sortedCategories)

                    Log.d("FetchProducts", "Fetched PARTNERS: ${sortedCategories} documents")

                }catch (e:Exception){

                    _products.value = NetworkResult.Error(e.message)
                }
            }

        }

    }