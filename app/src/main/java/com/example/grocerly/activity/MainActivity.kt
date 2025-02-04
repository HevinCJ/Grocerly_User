package com.example.grocerly.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import com.example.grocerly.R
import com.example.grocerly.ViewPagerSwipeControl
import com.example.grocerly.adapters.FragmentStateAdaptor
import com.example.grocerly.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity(),ViewPagerSwipeControl {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)



        binding.apply {

            val myAdapter = FragmentStateAdaptor(this@MainActivity)
            viewpgrmain.adapter = myAdapter

            TabLayoutMediator(tabLayoutmain, viewpgrmain) { tab, position ->
                when (position) {
                    0 -> {
                        tab.text = "Home"
                        tab.setIcon(R.drawable.homebtm)
                    }

                    1 -> {
                        tab.text = "Favourites"
                        tab.setIcon(R.drawable.favourites)
                    }

                    2 -> {
                        tab.text = "Search"
                        tab.setIcon(R.drawable.search)
                    }

                    3 -> {
                        tab.text = "Profile"
                        tab.setIcon(R.drawable.profile)
                    }

                    4 -> {
                        tab.text = "Menu"
                        tab.setIcon(R.drawable.menubtm)

                    }

                }
            }.attach()


        }

    }

    override fun setViewPagerEnabled(enabled: Boolean) {
        binding.viewpgrmain.isUserInputEnabled = enabled
    }
}


