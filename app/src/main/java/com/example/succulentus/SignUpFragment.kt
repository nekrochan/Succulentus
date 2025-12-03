package com.example.succulentus

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController

class SignUpFragment : LoggingFragment() {

    private lateinit var editTextUsernameNew: EditText
    private lateinit var editTextTextEmailAddress: EditText
    private lateinit var editTextPasswordNew: EditText
    private lateinit var editTextPasswordNewConfirm: EditText

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sign_up, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        editTextUsernameNew = view.findViewById(R.id.editTextUsernameNew)
        editTextTextEmailAddress = view.findViewById(R.id.editTextTextEmailAddress)
        editTextPasswordNew = view.findViewById(R.id.editTextPasswordNew)
        editTextPasswordNewConfirm = view.findViewById(R.id.editTextPasswordNewConfirm)

        val buttonSigningUp = view.findViewById<Button>(R.id.buttonSigningUp)
        buttonSigningUp.setOnClickListener {
            if (validateSignUpData()) {
                val user = User(
                    email = editTextTextEmailAddress.text.toString().trim(),
                    password = editTextPasswordNew.text.toString().trim(),
                    username = editTextUsernameNew.text.toString().trim()
                )
                if (registerNewUser(user)) {
                    // Навигация обратно к LoginFragment с передачей данных
                    val action = SignUpFragmentDirections.actionSignUpFragmentToLoginFragment(user)
                    findNavController().navigate(action)
                }
            }
        }

        val buttonHaveAccount = view.findViewById<Button>(R.id.buttonHaveAccount)
        buttonHaveAccount.setOnClickListener {
            // Навигация обратно к LoginFragment
            findNavController().navigate(R.id.action_signUpFragment_to_loginFragment)
        }
    }

    private fun validateSignUpData(): Boolean {
        val username = editTextUsernameNew.text.toString().trim()
        val email = editTextTextEmailAddress.text.toString().trim()
        val password = editTextPasswordNew.text.toString().trim()
        val passwordConfirm = editTextPasswordNewConfirm.text.toString().trim()

        if (username.isEmpty()) {
            showToast("Введите имя пользователя")
            editTextUsernameNew.requestFocus()
            return false
        }

        if (email.isEmpty()) {
            showToast("Введите email")
            editTextTextEmailAddress.requestFocus()
            return false
        }

        if (password.isEmpty()) {
            showToast("Введите пароль")
            editTextPasswordNew.requestFocus()
            return false
        }

        if (passwordConfirm.isEmpty()) {
            showToast("Подтвердите пароль")
            editTextPasswordNewConfirm.requestFocus()
            return false
        }

        if (!isValidEmail(email)) {
            showToast("Введите корректный email")
            editTextTextEmailAddress.requestFocus()
            return false
        }

        if (username.length < 3) {
            showToast("Имя пользователя должно содержать минимум 3 символа")
            editTextUsernameNew.requestFocus()
            return false
        }

        if (password.length < 6) {
            showToast("Пароль должен содержать минимум 6 символов")
            editTextPasswordNew.requestFocus()
            return false
        }

        if (password != passwordConfirm) {
            showToast("Пароли не совпадают")
            editTextPasswordNewConfirm.requestFocus()
            return false
        }

        if (!isValidUsername(username)) {
            showToast("Имя пользователя должно содержать от 8 до 20 символов")
            editTextUsernameNew.requestFocus()
            return false
        }

        return true
    }

    private fun registerNewUser(user: User): Boolean {
        if (checkIfUserExists(user.username, user.email)) {
            showToast("Пользователь с таким именем или email уже существует")
            return false
        }

        if (saveUserToDatabase(user)) {
            showToast("Регистрация прошла успешно")
            return true
        } else {
            showToast("Ошибка регистрации. Попробуйте позже")
            return false
        }
    }

    private fun checkIfUserExists(username: String, email: String): Boolean {
        return false
    }

    private fun saveUserToDatabase(user: User): Boolean {
        return true
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isValidUsername(username: String): Boolean {
        return username.length in 8..20
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        fun newInstance(): SignUpFragment {
            return SignUpFragment()
        }
    }
}