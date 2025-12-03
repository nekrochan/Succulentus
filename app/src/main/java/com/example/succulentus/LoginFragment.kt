package com.example.succulentus

import LoggingFragment
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs

class LoginFragment : LoggingFragment() {

    private lateinit var editTextUsername: EditText
    private lateinit var editTextPassword: EditText
    private val args: LoginFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        editTextUsername = view.findViewById(R.id.editTextUsername)
        editTextPassword = view.findViewById(R.id.editTextPassword)

        // Получение данных пользователя через Safe Args
        args.user?.let {
            editTextUsername.setText(it.username)
            editTextPassword.setText(it.password)
        }

        val buttonSignIn = view.findViewById<Button>(R.id.buttonSignedIn)
        buttonSignIn.setOnClickListener {
            if (validateLoginData()) {
                val username = editTextUsername.text.toString().trim()
                // Навигация к HomeFragment с передачей данных через Safe Args
                val action = LoginFragmentDirections.actionLoginFragmentToHomeFragment(username)
                findNavController().navigate(action)
            }
        }

        val buttonSignUp = view.findViewById<Button>(R.id.buttonSignUp)
        buttonSignUp.setOnClickListener {
            // Навигация к SignUpFragment
            findNavController().navigate(R.id.action_loginFragment_to_signUpFragment)
        }
    }

    private fun validateLoginData(): Boolean {
        val username = editTextUsername.text.toString().trim()
        val password = editTextPassword.text.toString().trim()

        if (username.isEmpty()) {
            showToast("Введите username")
            editTextUsername.requestFocus()
            return false
        }

        if (password.isEmpty()) {
            showToast("Введите пароль")
            editTextPassword.requestFocus()
            return false
        }

        if (!isValidUsername(username)) {
            showToast("Введите корректный username")
            editTextUsername.requestFocus()
            return false
        }

        if (password.length < 5) {
            showToast("Пароль должен содержать минимум 5 символов")
            editTextPassword.requestFocus()
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