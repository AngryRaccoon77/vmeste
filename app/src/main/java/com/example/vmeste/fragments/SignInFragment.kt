package com.example.vmeste.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.vmeste.databinding.FragmentSignInBinding
import com.example.vmeste.R
import com.example.vmeste.models.User

class SignInFragment : Fragment() {

    companion object {
        const val REQUEST_SIGN_UP = 1
        const val TAG = "SignInFragment"
    }

    private var _binding: FragmentSignInBinding? = null
    private val binding get() = _binding ?: throw IllegalStateException("Binding is null")
    private var registeredUser: User? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignInBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val args: SignInFragmentArgs by navArgs()
        registeredUser = args.user

        binding.buttonSignIn.setOnClickListener {
            val email = binding.emailInput.text.toString()
            val password = binding.passwordInput.text.toString()

            if (email == registeredUser?.email && password == registeredUser?.password) {
                findNavController().navigate(R.id.action_signInFragment_to_homeFragment)
            } else {
                Toast.makeText(
                    context,
                    "Неверный адрес электронной почты или пароль",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        binding.buttonSignUp.setOnClickListener {
            findNavController().navigate(R.id.action_signInFragment_to_signUpFragment)
        }

        Log.d(TAG, "onViewCreated called")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}