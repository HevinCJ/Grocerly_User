package com.example.grocerly.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.grocerly.fragments.Favourites
import com.example.grocerly.fragments.Menu
import com.example.grocerly.fragments.Profile
import com.example.grocerly.fragments.Search
import com.example.grocerly.fragments.Home

class FragmentStateAdaptor(fragmentActivity: FragmentActivity): FragmentStateAdapter(fragmentActivity) {
    override fun createFragment(position: Int): Fragment {
        return when(position){
            0-> Home()
            1-> Favourites()
            2-> Search()
            3-> Profile()
            4-> Menu()

            else->Home()
        }
    }

    override fun getItemCount(): Int {
      return 5
    }


}