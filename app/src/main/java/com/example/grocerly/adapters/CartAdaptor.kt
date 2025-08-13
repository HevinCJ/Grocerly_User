package com.example.grocerly.adapters

import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.StyleSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.grocerly.CheckoutListener
import com.example.grocerly.adapters.CartAdaptor.CartViewHolder
import com.example.grocerly.databinding.CartItemLayoutBinding
import com.example.grocerly.model.CartProduct
import com.example.grocerly.viewmodel.CartViewModel
import kotlinx.coroutines.flow.collectLatest

class CartAdaptor(private val listener: CheckoutListener): RecyclerView.Adapter<CartViewHolder>() {

    private var cartItems: List<CartProduct> = emptyList()

     class CartViewHolder( val binding: CartItemLayoutBinding): ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CartViewHolder {
       return CartViewHolder(CartItemLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(
        holder: CartViewHolder,
        position: Int
    ) {
        val currentItem = cartItems[position]
       holder.binding.apply {
           txtviewproductname.text = currentItem.product.itemName
           txtviewPrice.text = currentItem.product.itemPrice?.toString()
           txtviewquantity.text = currentItem.quantity.toString()

           val deliveryText = SpannableStringBuilder().apply {
               append("Delivery by ")

               val start = length
               append(currentItem.deliveryDate)
               val end = length

               setSpan(
                   StyleSpan(Typeface.BOLD),
                   start,
                   end,
                   Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
               )


           }
           txtviewexpecteddelivery.text = deliveryText

           txviewupdatequantity.setOnClickListener {
             val newQuantity = currentItem.quantity+1
              val newProduct = currentItem.copy(quantity = newQuantity)
               listener.onQuantityChanged(newProduct)
           }

           Glide.with(holder.binding.root)
               .load(currentItem.product.image)
               .into(imgviewcartitem)


           deletebtn.setOnClickListener {
              listener.onItemDeleted(currentItem)
           }
       }
    }

    override fun getItemCount(): Int {
        return cartItems.size
    }

    fun setCartItems(cartProduct: List<CartProduct>){
        cartItems = cartProduct
        notifyDataSetChanged()
    }

}