package com.example.chatwithbranch.presentation.login

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.chatwithbranch.R
import com.example.chatwithbranch.databinding.FragmentLoginBinding
import com.example.chatwithbranch.utils.AppConstants
import dagger.hilt.android.AndroidEntryPoint

/**
 * Fragment responsible for user login.
 * Manages the UI, login functionality, and navigation based on login status.
 */
@AndroidEntryPoint
class LoginFragment : Fragment() {

    private val viewModel: LoginViewModel by viewModels()
    private lateinit var binding: FragmentLoginBinding
    private lateinit var sharedPreference: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        binding = FragmentLoginBinding.inflate(layoutInflater,container,false)
        val sharedPreference =  requireActivity().getSharedPreferences(AppConstants.SHARED_PREF_NAME, Context.MODE_PRIVATE)
        val token = sharedPreference.getString(AppConstants.TOKEN,null)
        if (token != null){
            findNavController().navigate(R.id.action_loginFragment_to_messageThreadFragment)
        }
        initObservers()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPreference =  requireActivity().getSharedPreferences(AppConstants.SHARED_PREF_NAME, Context.MODE_PRIVATE)

        binding.btnLogin.setOnClickListener {
            val userName = binding.etUserName.text.toString()
            val pass =  binding.etPassword.text.toString()
            viewModel.login(userName,pass)
        }

    }

    /**
     * Initializes observers for LiveData updates from the ViewModel.
     */
    private fun initObservers() {
        viewModel.showProgress.observe(viewLifecycleOwner) { showProgress ->
            binding.progressBar.visibility = if (showProgress) View.VISIBLE else View.GONE
        }

        viewModel.loginResponse.observe(viewLifecycleOwner){
            if (it != null){
                sharedPreference.edit().putString(AppConstants.TOKEN,it.authToken).apply()
                findNavController().navigate(R.id.action_loginFragment_to_messageThreadFragment)
            }
            else{
                Toast.makeText(requireContext(),"Invalid username or password", Toast.LENGTH_SHORT).show()
            }
        }
    }
}