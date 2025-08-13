package com.example.grocerly.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.AsyncListUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.grocerly.databinding.SearchItemLayoutBinding
import com.example.grocerly.model.CartProduct
import com.example.grocerly.model.Product
import com.example.grocerly.viewmodel.CartViewModel

class SearchAdaptor(private val cartViewModel: CartViewModel): RecyclerView.Adapter<SearchAdaptor.SearchViewHolder>() {

    private val diffUtil = object : DiffUtil.ItemCallback<Product>(){
        override fun areItemsTheSame(
            oldItem: Product,
            newItem: Product
        ): Boolean {
          return  oldItem.productId == newItem.productId
        }

        override fun areContentsTheSame(
            oldItem: Product,
            newItem: Product
        ): Boolean {
         return  oldItem == newItem
        }

    }

    val products = AsyncListDiffer(this,diffUtil)

    inner class SearchViewHolder(private val binding: SearchItemLayoutBinding): RecyclerView.ViewHolder(binding.root){

        fun setProduct(product: Product){
            binding.apply {
                searchProduct = product
                binding.executePendingBindings()

                txtviewproductname.text = product.itemName.toString()
                txtviewPrice.text = product.itemPrice.toString()

                addfavouritetocartbtn.setOnClickListener {
                    cartViewModel.addProductIntoCartFirebase(CartProduct(product,1))
                }

                Glide.with(binding.root.context)
                    .load(product.image)
                    .into(binding.imgviewcartitem)

            }

        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SearchViewHolder {
       return SearchViewHolder(SearchItemLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(
        holder: SearchViewHolder,
        position: Int
    ) {
        val product = products.currentList[position]
        holder.setProduct(product)
    }

    override fun getItemCount(): Int {
       return products.currentList.size
    }

    fun setProducts(values: List<Product>?){
        products.submitList(values ?: emptyList())
    }


}