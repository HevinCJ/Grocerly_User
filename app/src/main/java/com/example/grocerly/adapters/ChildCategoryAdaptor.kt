package com.example.grocerly.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.grocerly.databinding.ChildcategoryLayoutBinding
import com.example.grocerly.model.ChildCategoryItem

class ChildCategoryAdaptor(): RecyclerView.Adapter<ChildCategoryAdaptor.ChildCategoryViewHolder>() {

    private var childItemList: List<ChildCategoryItem> = emptyList()

    class ChildCategoryViewHolder(private val binding: ChildcategoryLayoutBinding):ViewHolder(binding.root){

        fun setItem(childCategoryItem: ChildCategoryItem){
            binding.categoryItem = childCategoryItem
            binding.executePendingBindings()
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
        val currentChildItem = childItemList[position]
        holder.setItem(currentChildItem)
    }

    override fun getItemCount(): Int {
       return childItemList.size
    }


    fun setChildItems(childCategoryItem: List<ChildCategoryItem>): ChildCategoryAdaptor{
        this.childItemList = childCategoryItem
        notifyDataSetChanged()
        return this
    }

}