    package com.example.grocerly.Repository

    import android.util.Log
    import androidx.compose.ui.text.font.FontLoadingStrategy.Companion.Async
    import com.example.grocerly.model.CartProduct
    import com.example.grocerly.model.DeliveryCharge
    import com.example.grocerly.model.Product
    import com.example.grocerly.utils.Constants.CART
    import com.example.grocerly.utils.Constants.PARTNERS
    import com.example.grocerly.utils.Constants.PRODUCTS
    import com.example.grocerly.utils.Constants.QUANTITY
    import com.example.grocerly.utils.Constants.USERS
    import com.example.grocerly.utils.NetworkResult
    import com.google.firebase.auth.FirebaseAuth
    import com.google.firebase.firestore.DocumentReference
    import com.google.firebase.firestore.FieldValue
    import com.google.firebase.firestore.FirebaseFirestore
    import com.google.firebase.firestore.QuerySnapshot
    import com.google.firebase.firestore.SetOptions
    import com.google.firebase.firestore.toObject
    import com.google.firebase.firestore.toObjects
    import kotlinx.coroutines.CancellationException
    import kotlinx.coroutines.Dispatchers
    import kotlinx.coroutines.Job
    import kotlinx.coroutines.async
    import kotlinx.coroutines.awaitAll
    import kotlinx.coroutines.cancel
    import kotlinx.coroutines.channels.awaitClose
    import kotlinx.coroutines.coroutineScope
    import kotlinx.coroutines.delay
    import kotlinx.coroutines.flow.Flow
    import kotlinx.coroutines.flow.callbackFlow
    import kotlinx.coroutines.flow.first
    import kotlinx.coroutines.isActive
    import kotlinx.coroutines.joinAll
    import kotlinx.coroutines.launch
    import kotlinx.coroutines.suspendCancellableCoroutine
    import kotlinx.coroutines.tasks.await
    import org.jetbrains.annotations.Async
    import java.text.SimpleDateFormat
    import java.util.Calendar
    import java.util.Locale
    import javax.inject.Inject
    import kotlin.coroutines.resume
    import kotlin.math.roundToInt

    class CartRepoImpl @Inject constructor(
        private val db: FirebaseFirestore,
        private val auth: FirebaseAuth
    ) {

        val userId = auth.currentUser?.uid.toString()

        private val cartRef= db.collection(USERS).document(userId).collection(CART)


        suspend fun addProductToCart(cartProduct: CartProduct): NetworkResult<Unit> {
            return try {

                val existingDoc = cartRef.document(cartProduct.product.productId).get().await()
                val existingProduct = existingDoc.toObject(CartProduct::class.java)

                if (existingProduct?.product?.productId == cartProduct.product.productId) {



                    val newQuantity = existingProduct.quantity + 1
                    val newProduct = existingProduct.copy(quantity = newQuantity, deliveryDate = getFutureDateString(2,"dd MMMM, E"))
                    if (newProduct.quantity<=10){
                        cartRef.document(existingProduct.product.productId).update(newProduct.toHashMap()).await()
                        NetworkResult.Success(Unit)
                    }else{
                        NetworkResult.Error("Maximum Quantity")
                    }

                } else {
                    val updated = cartProduct.copy(deliveryDate = getFutureDateString(2,"dd MMMM, E"))

                    cartRef.document(cartProduct.product.productId).set(updated).await()
                    NetworkResult.Success(Unit)

                }



            }catch (e: Exception) {
                NetworkResult.Error(e.message ?: "Unknown Error Occurred")
            }

        }

        private fun CartProduct.toHashMap(): HashMap<String, Any>{
            return hashMapOf(
                "product" to product,
                "deliveryDate" to deliveryDate
            )
        }

        suspend fun updateQuantity(cartProduct: CartProduct): NetworkResult<Unit> {
            val maxQuantity = 10
            val documentRef: DocumentReference = cartRef.document(cartProduct.product.productId)
            return try {

                val updatedQuantity = cartProduct.quantity.coerceAtMost(maxQuantity)
                documentRef.update(QUANTITY, updatedQuantity).await()

                if (cartProduct.quantity > maxQuantity) {
                    NetworkResult.Error("Maximum quantity allowed for \n${cartProduct.product.itemName} is $maxQuantity.")
                } else {
                    NetworkResult.Success(Unit)
                }

            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                NetworkResult.Error(e.message.toString())
            }
        }


        fun fetchAllCartItems(): Flow<NetworkResult<List<CartProduct>>> = callbackFlow {
            var sycJob: Job?=null
                val listener = cartRef.addSnapshotListener { snapshot, exception ->
                    if (exception != null) {
                        trySend(
                            NetworkResult.Error(
                                exception.message ?: "Unable to fetch Products,Please try later...."
                            )
                        )
                    }

                    if (snapshot == null || snapshot.isEmpty) {
                       trySend(NetworkResult.Success(emptyList()))
                    }

                    snapshot?.let {
                        val cartProducts = snapshot.documents.mapNotNull { it.toObject(CartProduct::class.java) }

                        sycJob = launch {
                            updateCartItemsWithCurrentData(cartProducts)
                        }
                        trySend(NetworkResult.Success(cartProducts))

                    }


                }
                awaitClose {
                    sycJob?.cancel()
                    listener.remove()
                }
        }


        suspend fun updateCartItemsWithCurrentData(cartItems: List<CartProduct>) = coroutineScope{
            if (cartItems.isEmpty()) return@coroutineScope

           val grouped = cartItems
               .groupBy { it.product.partnerId }
               .flatMap { (partnerId,items) ->
                   items.chunked(10).map { chunk ->
                       async {
                           try {
                               val productSnapshot = db.collection(PARTNERS)
                                   .document(partnerId)
                                   .collection(PRODUCTS)
                                   .whereIn("productId", chunk.map { it.product.productId })
                                   .get()
                                   .await()

                               productSnapshot.toObjects(Product::class.java)
                           } catch (e: Exception) {
                               emptyList<Product>()
                           }
                       }
                   }

               }.awaitAll().flatten()


            val productMap = grouped.associateBy { it.productId  }
            Log.d("productmap",productMap.toString())
            cartItems.map { cartItem ->
                async {
                   try {

                       val updatedProduct = productMap[cartItem.product.productId]

                       if (updatedProduct == null) {
                          val itemToDelete =  cartRef.document(cartItem.product.productId).get().await()

                           if (itemToDelete.exists()){
                               cartRef.document(cartItem.product.productId).delete().await()
                           }

                       } else {
                           val updatedCartItem = cartItem.copy(
                               deliveryDate = getFutureDateString(2,"dd MMMM, E"),
                               product = updatedProduct.copy(
                                   productId = cartItem.product.productId,
                                   image = updatedProduct.image,
                                   itemName = updatedProduct.itemName ,
                                   itemPrice = updatedProduct.itemPrice,
                               )
                           )

                           if (updatedCartItem != cartItem) {
                               cartRef.document(cartItem.product.productId)
                                   .set(updatedCartItem, SetOptions.merge())
                                   .await()
                           }
                       }
                   }catch (e: Exception){
                       Log.e("CartUpdateError", "Failed to update cart item ${cartItem.product.productId}", e)
                   }
                }
            }.awaitAll()

        }


        suspend fun deleteItemFromCart(cartProduct: CartProduct): NetworkResult<Unit>{
            return try {
                    cartRef
                    .document(cartProduct.product.productId)
                    .delete()
                    .await()

                NetworkResult.Success(Unit)
            }catch (e: Exception){
                NetworkResult.Error(e.message)
            }
        }

        fun fetchTotalAmountFromCart(): Flow<NetworkResult<Float>> = callbackFlow {

                val listener = cartRef.addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        trySend(NetworkResult.Error(error.message ?: "Unable to fetch amount"))
                        return@addSnapshotListener
                    }

                snapshot?.let {
                    val amount = it.documents.mapNotNull { doc -> doc.toObject(CartProduct::class.java) }
                        .sumOf {cartProduct->
                            (cartProduct.product.itemPrice ?: 0) * (cartProduct.quantity ?: 1)
                        }
                        .toFloat()

                    trySend(NetworkResult.Success(amount))

                }

                }

                awaitClose {
                    listener.remove()
                }
        }


        fun fetchTotalPriceFromDb(cartItems: List<CartProduct>,couponAmount: Int = 0): Flow<NetworkResult<Map<String, Int>>> = callbackFlow {

                val listener = cartRef.addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        trySend(NetworkResult.Error(error.message ?: "Unable to fetch Total Amount\n Please try later..."))
                        return@addSnapshotListener
                    }

                    snapshot?.let {
                        val amount = it.documents.mapNotNull { doc -> doc.toObject(CartProduct::class.java) }
                            .sumOf {cartProduct->
                                (cartProduct.product.itemPrice ?: 0) * (cartProduct.quantity ?: 1)
                            }

                        val totalPrice = amount
                        val discountAmount = if (totalPrice > 500) (totalPrice * 0.15f).roundToInt() else 0
                        val platformFee = (totalPrice * 0.01f).roundToInt()
                        val deliveryFee = calculateDeliveryCharge(totalPrice)
                        val coupon = couponAmount
                        val finalAmount = (totalPrice + platformFee + deliveryFee.totalCharge) - coupon - discountAmount


                        val priceMap: Map<String, Int> = linkedMapOf(
                            "Price (${cartItems.size} Items)" to totalPrice,
                            "Product Discount" to discountAmount,
                            "Platform Fee" to platformFee,
                            deliveryFee.chargeType to deliveryFee.totalCharge,
                            "Applied Coupons" to coupon,
                            "Total Amount" to finalAmount
                        )


                        trySend(NetworkResult.Success(priceMap))

                    }

                }

                awaitClose {
                    listener.remove()
                }

        }

        fun getFutureDateString(daysAhead: Int, format: String = "dd MMMM, E"): String {

            val now = Calendar.getInstance()
            val noon = now.clone() as Calendar
            noon.set(Calendar.HOUR_OF_DAY,12)
            noon.set(Calendar.MINUTE, 0)
            noon.set(Calendar.SECOND, 0)
            noon.set(Calendar.MILLISECOND, 0)

            val adjustedDay = if (now.after(noon)) daysAhead+1 else daysAhead


            return SimpleDateFormat(format, Locale.getDefault()).format(
                Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, adjustedDay) }.time
            )
        }



        fun calculateDeliveryCharge(price:Int): DeliveryCharge{
            return when{
                price<500 ->  DeliveryCharge(40,"Standard delivery charge")
                price>=500 && price<800 -> DeliveryCharge(0,"Delivery charge")
                price>=800 && price<1000 -> DeliveryCharge(69,"Secured packaging fee")
                price>=1000 -> DeliveryCharge(89,"Promise protection fee")
                else -> DeliveryCharge()
            }
        }

    }