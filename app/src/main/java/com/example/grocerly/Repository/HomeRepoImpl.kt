package com.example.grocerly.Repository

import android.util.Log
import com.example.grocerly.model.Category
import com.example.grocerly.model.OfferItem
import com.example.grocerly.model.ParentCategoryItem
import com.example.grocerly.model.Product
import com.example.grocerly.utils.Constants.OFFERS
import com.example.grocerly.utils.Constants.PRODUCTS
import com.example.grocerly.utils.NetworkResult
import com.example.grocerly.utils.ProductCategory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.tasks.await
import javax.inject.Inject



class HomeRepoImpl @Inject constructor(private val auth: FirebaseAuth,private val db:FirebaseFirestore,private val addressRepoImpl: SavedAddressRepoImpl) {


     fun fetchProductFromFirebase(): Flow<NetworkResult<List<ParentCategoryItem>>> = callbackFlow {

             val listener = db.collectionGroup(PRODUCTS).addSnapshotListener { snapshot, error ->
                 if (error != null) {
                     trySend(NetworkResult.Error(error.message)).isFailure
                     return@addSnapshotListener
                 }

                 snapshot?.let {
                     val groupedProducts = it.toObjects(Product::class.java).groupBy {
                         it.category
                     }

                     val categories = groupedProducts.map { (category, products) ->
                         ParentCategoryItem(
                             categoryName = category.displayName,
                             childCategoryItems = products
                         )
                     }.sortedBy { it.categoryName }

                     trySend(NetworkResult.Success(categories))
                 }



             }

             awaitClose{
                 listener.remove()
             }
    }

    fun fetchByCategoryFromFirebase(category: ProductCategory): Flow<NetworkResult<List<Product>>> = callbackFlow {

        val listener = db.collectionGroup(PRODUCTS).addSnapshotListener { snapshot, error ->
            if (error != null) {
                trySend(NetworkResult.Error(error.message)).isFailure
                return@addSnapshotListener
            }

            snapshot?.let {
                val groupedProducts = it.toObjects(Product::class.java).filter {
                    it.category == category
                }


                trySend(NetworkResult.Success(groupedProducts))
            }



        }

        awaitClose{
            listener.remove()
        }
    }


    suspend fun getOffersFromFirebase():NetworkResult<List<OfferItem>>{
        return try {

            val querySnapshot = db.collectionGroup(OFFERS)
                .get()
                .await()

            val fetchedOffers = querySnapshot.toObjects(OfferItem::class.java)
            Log.d("currentitemrepo",fetchedOffers.toString())
            NetworkResult.Success(fetchedOffers)


        }catch (E:Exception){
            NetworkResult.Error(E.message)
        }


    }


    suspend fun getCityAndState(): NetworkResult<String>{
      return try {
          val address =  addressRepoImpl.getDefaultAddressFromDb().firstOrNull()?.data
          if (address==null){
              NetworkResult.Success("")
          }
          
          val formatedAddress = buildString {
              append(address?.city)
              append(" , ")
              append(address?.state)
          }
          NetworkResult.Success(formatedAddress)
      }catch (e: Exception){
          NetworkResult.Error(e.message)
      }
    }




}