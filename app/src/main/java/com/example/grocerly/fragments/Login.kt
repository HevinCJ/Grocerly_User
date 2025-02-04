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
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.grocerly.R
import com.example.grocerly.activity.MainActivity
import com.example.grocerly.databinding.FragmentLoginBinding
import com.example.grocerly.utils.NetworkResult
import com.example.grocerly.utils.RegisterValidation
import com.example.grocerly.viewmodel.LoginViewModel
import com.example.grocerly.viewmodel.SharedViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class Login : Fragment() {
    private var login:FragmentLoginBinding?=null
    private val binding get() = login!!

    private val loginViewModel: LoginViewModel by viewModels()
    private val sharedViewModel:SharedViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
       login = FragmentLoginBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loginToFirebase()
        observeLoginState()
        observeValidationState()
        actionLoginToSignUp()
    }

    private fun actionLoginToSignUp() {
        binding.apply {
            actiontosignuptxtview.setOnClickListener{
                findNavController().navigate(R.id.action_login_to_signUp)
            }
        }
    }

    private fun observeValidationState() {
        lifecycleScope.launch {
            loginViewModel.validationState.collect{state->
                if (state.email is RegisterValidation.Failed){
                    binding.edttxtemail.apply {
                        requestFocus()
                        error = state.email.message
                    }
                }

                if (state.password is RegisterValidation.Failed){
                    binding.edttxtpassword.apply {
                        requestFocus()
                        error = state.password.message
                    }
                }
            }
        }
    }

    private fun observeLoginState() {
      lifecycleScope.launch{
          loginViewModel.loginstate.collect{ result->
              when(result){
                  is NetworkResult.Error -> {
                      Toast.makeText(requireContext(),result.message,Toast.LENGTH_SHORT).show()
                  }
                  is NetworkResult.Loading -> {
                      Toast.makeText(requireContext(),"Loading,Please wait...",Toast.LENGTH_SHORT).show()
                  }
                  is NetworkResult.Success -> {
                    setPopUpToHomeFragment()
                  }
                else -> {

                }
              }

          }
      }
    }

    private fun loginToFirebase() {
        binding.apply {
          loginbtn.setOnClickListener{
              if (sharedViewModel.isNetworkAvailable(requireContext())){
                  val email = edttxtemail.text.toString().trim()
                  val password = edttxtpassword.text.toString().trim()
                  loginViewModel.loginUserIntoFirebase(email,password)
              }else{
                  Toast.makeText(requireContext(),"Please Enable Wifi/Data Connection",Toast.LENGTH_SHORT).show()
              }
          }
        }
    }

    private fun setPopUpToHomeFragment(){
       val intent = Intent(requireActivity(), MainActivity::class.java)
        startActivity(intent)
            requireActivity().finish()

    }

}