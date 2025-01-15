package com.example.grocerly.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.grocerly.databinding.ParentCategoryLayoutBinding
import com.example.grocerly.model.ParentCategoryItem

class ParentCategoryAdaptor(): RecyclerView.Adapter<ParentCategoryAdaptor.ParentCategoryViewHolder>() {

    private var ParentItems: List<ParentCategoryItem> = emptyList()

     class ParentCategoryViewHolder( private val binding: ParentCategoryLayoutBinding):ViewHolder(binding.root){

         fun bindCategoryItem(parentCategoryItem: ParentCategoryItem){
             binding.apply {

                 txtviewCategoryItems.text = parentCategoryItem.categoryName
                 val childAdapter = ChildCategoryAdaptor().setChildItems(parentCategoryItem.childCategoryItems)
                 rcViewChildItems.adapter = childAdapter
                 rcViewChildItems.layoutManager = LinearLayoutManager(binding.root.context, RecyclerView.HORIZONTAL, false)
             }
         }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ParentCategoryViewHolder {
       return ParentCategoryViewHolder(ParentCategoryLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(
        holder: ParentCategoryViewHolder,
        position: Int
    ) {

        val currentCategory = ParentItems[position]
        holder.bindCategoryItem(currentCategory)
    }

    override fun getItemCount(): Int {
        return ParentItems.size
    }

    fun setParentItems(parentCategoryItem: List<ParentCategoryItem>){
        this.ParentItems = parentCategoryItem
        notifyDataSetChanged()
    }

}