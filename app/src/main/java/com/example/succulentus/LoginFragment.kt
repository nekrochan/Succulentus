package com.example.succulentus

import android.os.Bundle
import android.widget.Toast
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.succulentus.databinding.FragmentLoginBinding

class LoginFragment : LoggingFragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val args: LoginFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //баннинг здесь
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Получение данных пользователя через Safe Args
        args.user?.let {
            //баннинг здесь
            binding.editTextUsername.setText(it.username)
            binding.editTextPassword.setText(it.password)
        }

        binding.buttonSignedIn.setOnClickListener {
            if (validateLoginData()) {
                //баннинг здесь и тд
                val username = binding.editTextUsername.text.toString().trim()
                // Навигация к HomeFragment с передачей данных через Safe Args
                val action = LoginFragmentDirections.actionLoginFragmentToHomeFragment(username)
                findNavController().navigate(action)
            }
        }

        binding.buttonSignUp.setOnClickListener {
            // Навигация к SignUpFragment
            findNavController().navigate(R.id.action_loginFragment_to_signUpFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun validateLoginData(): Boolean {
        val username = binding.editTextUsername.text.toString().trim()
        val password = binding.editTextPassword.text.toString().trim()

        if (username.isEmpty()) {
            showToast("Введите username")
            binding.editTextUsername.requestFocus()
            return false
        }

        if (password.isEmpty()) {
            showToast("Введите пароль")
            binding.editTextPassword.requestFocus()
            return false
        }

        if (!isValidUsername(username)) {
            showToast("Введите корректный username")
            binding.editTextUsername.requestFocus()
            return false
        }

        if (password.length < 5) {
            showToast("Пароль должен содержать минимум 5 символов")
            binding.editTextPassword.requestFocus()
            return false
        }

        if (!checkUserInDatabase(username, password)) {
            showToast("Неверный username или пароль")
            return false
        }

        return true
    }

    private fun isValidUsername(username: String): Boolean {
        return true
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun checkUserInDatabase(username: String, password: String): Boolean {
        return true
    }

    companion object {
        fun newInstance(user: User? = null): LoginFragment {
            val fragment = LoginFragment()
            return fragment
        }
    }
}