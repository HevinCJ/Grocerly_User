package com.example.grocerly.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.grocerly.databinding.OffersRcviewBinding
import com.example.grocerly.model.OfferItem

class OffersAdaptor():RecyclerView.Adapter<OffersAdaptor.OfferViewHolder>() {

    private val offerItems: List<OfferItem> = emptyList()

    inner  class OfferViewHolder(private val binding: OffersRcviewBinding):ViewHolder(binding.root){

        fun bindOffer(offerItem: OfferItem){
            binding.offerItem = offerItem
            binding.executePendingBindings()
        }
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): OfferViewHolder {
        return OfferViewHolder(OffersRcviewBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(
        holder: OfferViewHolder,
        position: Int
    ) {

        val currentOffer = offerItems[position]
        holder.bindOffer(currentOffer)

    }

    override fun getItemCount(): Int {
        return offerItems.size
    }


}