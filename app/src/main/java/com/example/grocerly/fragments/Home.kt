package com.example.grocerly.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.grocerly.R
import com.example.grocerly.adapters.CategoryAdaptor
import com.example.grocerly.adapters.OffersAdaptor
import com.example.grocerly.adapters.ParentCategoryAdaptor
import com.example.grocerly.databinding.FragmentHomeBinding
import com.example.grocerly.model.Category
import com.example.grocerly.utils.NetworkResult
import com.example.grocerly.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class Home : Fragment() {
    private var home: FragmentHomeBinding?=null
    private val binding get() = home!!

    private val offersAdaptor: OffersAdaptor by lazy { OffersAdaptor() }
    private val categoryAdaptor: CategoryAdaptor by lazy { CategoryAdaptor() }

    private val homeViewModel:HomeViewModel by viewModels()
    private val parentCategoryAdaptor:ParentCategoryAdaptor by lazy { ParentCategoryAdaptor() }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
       home = FragmentHomeBinding.inflate(inflater,container,false)
        homeViewModel.fetchProductFromFirebase()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        setRcViewOfferAdapter()
        setRcViewCategoryItem()
        setCategoryItems()
        observeProductFromFirebase()
        setRcViewParentCategoryAdaptor()
    }

    override fun onResume() {
        super.onResume()
        homeViewModel.fetchProductFromFirebase()
    }

    private fun observeProductFromFirebase() {
        viewLifecycleOwner.lifecycleScope.launch {

            homeViewModel.products.collect{result->
                when(result){
                    is NetworkResult.Error -> {
                        Toast.makeText(requireContext(),result.message, Toast.LENGTH_SHORT).show()
                    }
                    is NetworkResult.Loading -> {
                        Toast.makeText(requireContext(),"Loading,Please wait..",Toast.LENGTH_SHORT).show()
                    }
                    is NetworkResult.Success -> {
                        result.data?.let {
                            parentCategoryAdaptor.setParentItems(it)
                        }

                    }
                    is NetworkResult.UnSpecified -> {

                    }
                }
            }
        }
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

    private fun setRcViewParentCategoryAdaptor(){
        binding.apply {
            nestedrcview.adapter = parentCategoryAdaptor
            nestedrcview.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
        }
    }




    private fun setRcViewCategoryItem() {
        binding.apply {
            rcviewCategory.adapter = categoryAdaptor
            rcviewCategory.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)


        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        home=null
    }
}