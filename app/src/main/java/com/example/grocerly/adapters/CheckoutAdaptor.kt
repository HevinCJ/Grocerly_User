package com.example.grocerly.adapters


import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.grocerly.CheckoutListener
import com.example.grocerly.databinding.CheckoutItemsLayoutBinding
import com.example.grocerly.model.CartProduct
import com.example.grocerly.model.Order
import okhttp3.internal.wait

class CheckoutAdaptor(private val listener: CheckoutListener): RecyclerView.Adapter<CheckoutAdaptor.checkoutViewHolder>() {

  private var checkoutList: List<CartProduct> = emptyList()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): checkoutViewHolder {
        return checkoutViewHolder(CheckoutItemsLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(
        holder: checkoutViewHolder,
        position: Int
    ) {
        val currentItem = checkoutList[position]
        holder.setItemData(currentItem)
    }

    override fun getItemCount(): Int {
       return checkoutList.size
    }

    inner class checkoutViewHolder(private val binding: CheckoutItemsLayoutBinding): RecyclerView.ViewHolder(binding.root){


        fun setItemData(item: CartProduct){
            binding.apply {
                txtviewproductname.text = item.product.itemName
                txtviewofferPrice.text = item.product.itemPrice.toString()
                txtviewquantity.text = item.quantity.toString()
                loadItemImage(item)
                setQuantityClickListener(item)
            }
        }

        private fun setQuantityClickListener(product: CartProduct) {
            binding.apply {
                txviewaddequantity.setOnClickListener {
                    val newQuantity = product.quantity + 1
                    val newProduct = product.copy(quantity = newQuantity)
                    listener.onQuantityChanged(newProduct)

                }

                txtviewreducequantity.setOnClickListener {
                    val newQuantity = product.quantity - 1
                    val newProduct = product.copy(quantity = newQuantity)
                    if (newQuantity>=1){
                        listener.onQuantityChanged(newProduct)
                    }else{
                        txtviewquantity.text = 0.toString()
                        listener.onItemDeleted(newProduct)
                    }
                }
            }
        }

        private fun loadItemImage(product: CartProduct) {
            Glide.with(binding.root.context)
                .load(product.product.image)
                .into(binding.imgviewcartitem)
                .onStart()

        }

    }


    fun setCartItems(order: List<CartProduct>){
       checkoutList = order
        notifyDataSetChanged()
    }
}