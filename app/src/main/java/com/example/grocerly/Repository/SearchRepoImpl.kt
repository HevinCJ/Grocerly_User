package com.example.grocerly.Repository

import androidx.compose.ui.text.toLowerCase
import androidx.compose.ui.text.toUpperCase
import com.example.grocerly.model.Product
import com.example.grocerly.utils.Constants.PRODUCTS
import com.example.grocerly.utils.NetworkResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class SearchRepoImpl@Inject constructor(private val db: FirebaseFirestore,private val auth: FirebaseAuth) {

    fun searchProduct(productName: String): Flow<NetworkResult<List<Product>>> = callbackFlow {

            val listener = db.collectionGroup(PRODUCTS)
                .whereGreaterThanOrEqualTo("itemName",productName)
                .whereLessThanOrEqualTo("itemName", productName + "\uf8ff")
                .limit(10)
                .addSnapshotListener { snapshot,error ->

                    if (error!=null){
                        trySend(NetworkResult.Error(error.message))
                        return@addSnapshotListener
                    }

                    snapshot?.let {
                        val productList = snapshot.documents.mapNotNull { it.toObject(Product::class.java) }
                        trySend(NetworkResult.Success(productList))
                    }


                }
            awaitClose{
                listener.remove()
            }
    }

}