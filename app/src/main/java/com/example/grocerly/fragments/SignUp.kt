package com.example.grocerly.fragments

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
import com.example.grocerly.databinding.FragmentSignUpBinding
import com.example.grocerly.model.Account

import com.example.grocerly.utils.NetworkResult
import com.example.grocerly.utils.RegisterFieldState
import com.example.grocerly.utils.RegisterValidation
import com.example.grocerly.viewmodel.SharedViewModel
import com.example.grocerly.viewmodel.SignUpViewmodel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SignUp : Fragment() {

    private var  signUp:FragmentSignUpBinding?=null
    private val binding get() = signUp!!

    private val sharedViewModel:SharedViewModel by viewModels()
    private val signUpViewmodel by viewModels<SignUpViewmodel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        signUp = FragmentSignUpBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        createUserWithPassword()
        observeSignUpData()
        observeValidationState()
        actionToLogin()
    }

    private fun actionToLogin() {
        binding.apply {
            actiontosignuptxtview.setOnClickListener {
                findNavController().navigate(R.id.action_signUp_to_login)
            }
        }
    }

    private fun observeSignUpData() {
       lifecycleScope.launch {
           signUpViewmodel.isSigned.collect{result->
               when(result){
                   is NetworkResult.Error -> {
                       Toast.makeText(requireContext(),result.message,Toast.LENGTH_SHORT).show()
                   }
                   is NetworkResult.Loading -> {
                       Toast.makeText(requireContext(),"Loading,Please wait...",Toast.LENGTH_SHORT).show()
                   }
                   is NetworkResult.Success -> {
                       findNavController().navigate(R.id.action_signUp_to_login)
                   }
                   else ->{

                   }
               }
           }
       }
    }

    private fun createUserWithPassword() {
        binding.apply {
         signupbtn.setOnClickListener {
             if (sharedViewModel.isNetworkAvailable(requireContext())){
                 val user = Account(  edttxtname.text.toString().trim(),"",edttxtemail.text.toString().trim(),"")
                 val password = edttxtpassword.text.toString().trim()
                 signUpViewmodel.createUser(user,password)
             }else{
                 Toast.makeText(requireContext(),"No Internet",Toast.LENGTH_SHORT).show()
             }
         }
        }
    }


    private fun observeValidationState(){
        lifecycleScope.launch {
            signUpViewmodel.validationState.collect{state->
                if(state.email is RegisterValidation.Failed){
                    binding.edttxtemail.apply {
                        requestFocus()
                        error = state.email.message
                    }
                }

                if(state.password is RegisterValidation.Failed){
                    binding.edttxtpassword.apply {
                        requestFocus()
                        error = state.password.message
                    }
                }

                if(state.name is RegisterValidation.Failed){
                    binding.edttxtname.apply {
                        requestFocus()
                        error = state.name.message
                    }
                }
            }
        }
    }
}