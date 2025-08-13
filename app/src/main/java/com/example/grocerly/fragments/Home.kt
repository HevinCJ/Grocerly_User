package com.example.grocerly.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import com.example.grocerly.R
import com.example.grocerly.adapters.CategoryAdaptor
import com.example.grocerly.adapters.OffersAdaptor
import com.example.grocerly.adapters.ParentCategoryAdaptor
import com.example.grocerly.databinding.FragmentHomeBinding
import com.example.grocerly.interfaces.ChildCategoryListener
import com.example.grocerly.model.CartProduct
import com.example.grocerly.model.Category
import com.example.grocerly.model.FavouriteItem
import com.example.grocerly.utils.NetworkResult
import com.example.grocerly.viewmodel.CartViewModel
import com.example.grocerly.viewmodel.FavouriteViewModel
import com.example.grocerly.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class Home : Fragment() {

    private var home: FragmentHomeBinding? = null
    private val binding get() = home!!

    private val offersAdaptor: OffersAdaptor by lazy { OffersAdaptor() }
    private val categoryAdaptor: CategoryAdaptor by lazy { CategoryAdaptor() }

    private val cartViewModel by activityViewModels<CartViewModel>()

    private val favouriteViewModel by activityViewModels<FavouriteViewModel>()

    private val homeViewModel: HomeViewModel by viewModels()

   private lateinit var parentCategoryAdaptor: ParentCategoryAdaptor

   private var isAutoScrolling = false
    private var currentScrollPosition = 0

    private val handler = Handler(Looper.getMainLooper())

   private val runnable = object : Runnable {
        override fun run() {
            try {
                if (offersAdaptor.itemCount == 0) return

                currentScrollPosition = (currentScrollPosition + 1) % offersAdaptor.itemCount
                binding.rcpageoffers.smoothScrollToPosition(currentScrollPosition)

                startAutoScroll()
            } catch (e: Exception) {
                Log.e("HomeFragment", "Error in auto-scroll: ${e.message}")
            }
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        home = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setRcOfferAdapter()
        setToolBar()
        setRcViewParentCategoryAdaptor()
        setRcViewCategoryItem()
        observeGetAllItems()
        setCategoryItems()
        observeProductFromFirebase()
        observeOffersFromFirebase()
        observeAddProductInCart()
        observeAddedToFavouriteState()
        showShimmerLayout()
        observeCartItems()
        observeHomeAddress()
    }

    private fun observeHomeAddress() {
        viewLifecycleOwner.lifecycleScope.launch {
            homeViewModel.homeAddress.collectLatest {
                if (it is NetworkResult.Success || it is  NetworkResult.Error){

                    if (it.data.isNullOrEmpty() || !it.message.isNullOrEmpty()){
                        binding.lnrlayoutaddress.visibility = View.INVISIBLE
                    }
                    binding.txtviewaddress.text = it.data

                }
            }
        }
    }

    private fun observeCartItems() {
        viewLifecycleOwner.lifecycleScope.launch {
            homeViewModel.cartItems.collectLatest {
                if (it is NetworkResult.Success || it is NetworkResult.Error){
                    it.data?.let { cartProducts ->
                        parentCategoryAdaptor.setCartItems(cartProducts)
                    }

                    it.message?.mapNotNull {
                        Toast.makeText(requireContext(),it.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun showShimmerLayout() {
        viewLifecycleOwner.lifecycleScope.launch {
            homeViewModel.products.collectLatest {
                if (it.data.isNullOrEmpty()){
                    binding.shimmerlayouthome.startShimmer()
                    binding.shimmerlayouthome.visibility = View.VISIBLE
                    binding.addresstoolbar.visibility = View.INVISIBLE
                    binding.scrollviewhome.visibility = View.INVISIBLE
                }else{
                    binding.shimmerlayouthome.stopShimmer()
                    binding.shimmerlayouthome.visibility = View.INVISIBLE
                    binding.addresstoolbar.visibility = View.VISIBLE
                    binding.scrollviewhome.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun observeGetAllItems() {
             viewLifecycleOwner.lifecycleScope.launch {
            favouriteViewModel.favouritesList.collectLatest { favourites->
                when(favourites){
                    is NetworkResult.Error<*> -> {

                    }
                    is NetworkResult.Loading<*> -> {

                    }
                    is NetworkResult.Success<*> -> {
                        favourites.data?.let {
                            parentCategoryAdaptor.setFavouriteItems(it)
                        }
                    }
                    is NetworkResult.UnSpecified<*> -> {

                    }
                }
            }
        }
    }

    private fun observeAddedToFavouriteState() {
       viewLifecycleOwner.lifecycleScope.launch {
           favouriteViewModel.favouritesState.collectLatest { favourites->
               when(favourites){
                   is NetworkResult.Error<*> -> {
                       Toast.makeText(requireContext(), favourites.message, Toast.LENGTH_SHORT).show()
                   }
                   is NetworkResult.Loading<*> -> {

                   }
                   is NetworkResult.Success<*> -> {
                      favourites.data?.let {
                          Toast.makeText(requireContext(), "Your Item (${it.product.itemName}) \nAdded to favourites", Toast.LENGTH_SHORT).show()
                      }
                   }
                   is NetworkResult.UnSpecified<*> -> {

                   }
               }
           }
       }
    }

    private fun observeAddProductInCart() {
        viewLifecycleOwner.lifecycleScope.launch {
            cartViewModel.addedCartItems.collectLatest{ result ->
                if (result is NetworkResult.Error){
                    Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setToolBar() {
        binding.apply {
            addresstoolbar.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.cartm -> {
                        findNavController().navigate(R.id.action_home_to_cart)
                        true
                    }

                    else -> false
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        startAutoScroll()
        favouriteViewModel.getAllFavouritesFromFirebase()
        homeViewModel.fetchOffersFromFirebase()
        homeViewModel.fetchProductFromFirebase()
    }
    private fun stopAutoScroll() {
        isAutoScrolling = false
        handler.removeCallbacks(runnable)
    }


    private fun startAutoScroll() {
        isAutoScrolling = true
        handler.postDelayed(runnable, 3000)
    }



    private fun observeOffersFromFirebase() {
        viewLifecycleOwner.lifecycleScope.launch {
            homeViewModel.offers.collectLatest { offers ->

                when (offers) {
                    is NetworkResult.Error -> {
                        Toast.makeText(requireContext(), offers.message, Toast.LENGTH_SHORT).show()
                    }

                    is NetworkResult.Loading -> {

                    }

                    is NetworkResult.Success -> {
                        offers.data?.let {
                            offersAdaptor.setOffers(it)
                            Log.d("currentitem",it.toString())
                        }

                    }

                    is NetworkResult.UnSpecified -> {

                    }
                }

            }
        }
    }


    private fun observeProductFromFirebase() {
        viewLifecycleOwner.lifecycleScope.launch {
            homeViewModel.products.collectLatest{ result ->

                when (result) {
                    is NetworkResult.Error -> {
                        Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
                    }

                    is NetworkResult.Loading -> {

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

    private fun setRcOfferAdapter() {
        binding.apply {
            rcpageoffers.adapter = offersAdaptor
           rcpageoffers.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL,false)
            rcpageoffers.setHasFixedSize(true)
            rcpageoffers.isNestedScrollingEnabled = false

            LinearSnapHelper().attachToRecyclerView(binding.rcpageoffers)
        }

    }

    private fun setRcViewParentCategoryAdaptor() {
        binding.apply {

            parentCategoryAdaptor = ParentCategoryAdaptor( object : ChildCategoryListener{
                override fun addProductToCart(cartProduct: CartProduct) {
                    cartViewModel.addProductIntoCartFirebase(cartProduct)
                }

                override fun addProductToFavourites(favouriteItem: FavouriteItem) {
                    favouriteViewModel.addToFavourites(favouriteItem)
                }


            })

            nestedrcview.adapter = parentCategoryAdaptor
            nestedrcview.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }
    }


    private fun setRcViewCategoryItem() {
        binding.apply {

            rcviewCategory.adapter = categoryAdaptor
            rcviewCategory.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)


        }
    }

    override fun onPause() {
        super.onPause()
        stopAutoScroll()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        stopAutoScroll()
        home = null
    }


}