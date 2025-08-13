package com.example.grocerly.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.grocerly.R
import com.example.grocerly.databinding.ChildcategoryLayoutBinding
import com.example.grocerly.interfaces.ChildCategoryListener
import com.example.grocerly.model.CartProduct
import com.example.grocerly.model.FavouriteItem
import com.example.grocerly.model.Product
import com.example.grocerly.viewmodel.CartViewModel
import com.example.grocerly.viewmodel.FavouriteViewModel
import kotlinx.coroutines.flow.collect

class ChildCategoryAdaptor(private val listener: ChildCategoryListener) :
    RecyclerView.Adapter<ChildCategoryAdaptor.ChildCategoryViewHolder>() {

    private var childItemList: List<Product> = emptyList()
    private var favoritesList: List<FavouriteItem> = emptyList()
    private var cartItems: List<CartProduct> = emptyList()



    inner class ChildCategoryViewHolder(private val binding: ChildcategoryLayoutBinding) :
        ViewHolder(binding.root) {


        fun setItem(childCategoryItem: Product) {
            binding.categoryItem = childCategoryItem
            binding.executePendingBindings()


            binding.addtocartbtn.setOnClickListener {
                listener.addProductToCart(CartProduct(childCategoryItem, 1))
            }
            binding.addtofavouritesbtn.setOnClickListener {
                listener.addProductToFavourites(
                    FavouriteItem(
                        childCategoryItem.productId,
                        childCategoryItem
                    )
                )
            }

            if (cartItems.any { it.product.productId == childCategoryItem.productId }){
                binding.addtocartbtn.setImageDrawable(ContextCompat.getDrawable(binding.root.context,R.drawable.checkcircleadded))
            }

            if (favoritesList.any { it.product.productId == childCategoryItem.productId }) {
                binding.addtofavouritesbtn.setColorFilter(
                    ContextCompat.getColor(
                        binding.root.context,
                        R.color.red
                    )
                )
            } else {
                binding.addtofavouritesbtn.clearColorFilter()
            }

        }


    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ChildCategoryViewHolder {

        return ChildCategoryViewHolder(
            ChildcategoryLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(
        holder: ChildCategoryViewHolder,
        position: Int
    ) {
        val product = childItemList[position]
        holder.setItem(product)


    }

    override fun getItemCount(): Int {
        return childItemList.size
    }


    fun setChildItems(childCategoryItem: List<Product>): ChildCategoryAdaptor {
        this.childItemList = childCategoryItem
        notifyDataSetChanged()
        return this
    }

    fun setFavouriteItems(favourites: List<FavouriteItem>) {
        favoritesList = favourites
        notifyDataSetChanged()
    }

    fun setCartItems(Items: List<CartProduct>) {
        cartItems = Items
        notifyDataSetChanged()
    }



}