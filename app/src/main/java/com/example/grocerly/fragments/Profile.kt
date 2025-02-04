package com.example.grocerly.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.grocerly.R
import com.example.grocerly.activity.LoginActivity
import com.example.grocerly.databinding.FragmentProfileBinding
import com.example.grocerly.utils.NetworkResult
import com.example.grocerly.viewmodel.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class Profile : Fragment() {
    private var profile:FragmentProfileBinding?=null
    private val binding get() = profile!!

    private val profileViewModel:ProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        profile = FragmentProfileBinding.inflate(inflater,container,false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpLoginOutButton()
        observeLogoutState()
    }

    private fun observeLogoutState() {
        lifecycleScope.launch {
            profileViewModel.logoutstate.collect{result->
                when(result){
                    is NetworkResult.Error -> {
                        Toast.makeText(requireContext(),result.message, Toast.LENGTH_SHORT).show()
                    }
                    is NetworkResult.Loading -> {
                        Toast.makeText(requireContext(),"Loading.. Please wait",Toast.LENGTH_SHORT).show()
                    }
                    is NetworkResult.Success -> {
                        Toast.makeText(requireContext(),result.data,Toast.LENGTH_SHORT).show()
                       val intent = Intent(requireActivity(),LoginActivity::class.java).apply {
                           putExtra("skip_splash",true)
                       }
                        startActivity(intent)
                        requireActivity().finish()
                    }
                    is NetworkResult.UnSpecified -> {

                    }
                }
            }
        }
    }

    private fun setUpLoginOutButton() {
        binding.apply {
            logoutbtn.setOnClickListener{
                profileViewModel.LogOutUserFromFirebase()
            }
        }
    }

}