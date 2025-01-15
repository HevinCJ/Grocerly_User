package com.example.grocerly.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.grocerly.R
import com.example.grocerly.adapters.CategoryAdaptor
import com.example.grocerly.adapters.OffersAdaptor
import com.example.grocerly.databinding.FragmentHomeBinding
import com.example.grocerly.model.Category

class Home : Fragment() {
    private var home: FragmentHomeBinding?=null
    private val binding get() = home!!

    private val offersAdaptor: OffersAdaptor by lazy { OffersAdaptor() }
    private val categoryAdaptor: CategoryAdaptor by lazy { CategoryAdaptor() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       home = FragmentHomeBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setRcViewOfferAdapter()
        setRcViewCategoryItem()
        setCategoryItems()
    }

    private fun setCategoryItems() {
        val items = listOf<Category>(
            Category("Fruits & Vegies", R.drawable.fruitsvegetables),
            Category("Frozen Foods ", R.drawable.frozenfood),
            Category("Bread & Bakery", R.drawable.bread),
            Category("Personal Care", R.drawable.personalcare),
            Category("Households", R.drawable.households),
            Category("HealthCare", R.drawable.healthcare),
            Category("Meat", R.drawable.meat)
        )

        categoryAdaptor.setItem(items)
    }

    private fun setRcViewOfferAdapter() {
        binding.apply {
            rcviewoffers.adapter = offersAdaptor
            rcviewoffers.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL,false)

        }

    }




    private fun setRcViewCategoryItem(){
        binding.apply {
            rcviewCategory.adapter = categoryAdaptor
            rcviewCategory.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL,false)

        }
    }

}