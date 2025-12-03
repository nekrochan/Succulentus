package com.example.succulentus

import android.os.Bundle
import android.widget.Toast
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.succulentus.databinding.FragmentSignUpBinding

class SignUpFragment : LoggingFragment() {

    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonSigningUp.setOnClickListener {
            if (validateSignUpData()) {
                val user = User(
                    email = binding.editTextTextEmailAddress.text.toString().trim(),
                    password = binding.editTextPasswordNew.text.toString().trim(),
                    username = binding.editTextUsernameNew.text.toString().trim()
                )
                if (registerNewUser(user)) {
                    // Навигация обратно к LoginFragment с передачей данных
                    val action = SignUpFragmentDirections.actionSignUpFragmentToLoginFragment(user)
                    findNavController().navigate(action)
                }
            }
        }

        binding.buttonHaveAccount.setOnClickListener {
            // Навигация обратно к LoginFragment
            findNavController().navigate(R.id.action_signUpFragment_to_loginFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun validateSignUpData(): Boolean {
        val username = binding.editTextUsernameNew.text.toString().trim()
        val email = binding.editTextTextEmailAddress.text.toString().trim()
        val password = binding.editTextPasswordNew.text.toString().trim()
        val passwordConfirm = binding.editTextPasswordNewConfirm.text.toString().trim()

        if (username.isEmpty()) {
            showToast("Введите имя пользователя")
            binding.editTextUsernameNew.requestFocus()
            return false
        }

        if (email.isEmpty()) {
            showToast("Введите email")
            binding.editTextTextEmailAddress.requestFocus()
            return false
        }

        if (password.isEmpty()) {
            showToast("Введите пароль")
            binding.editTextPasswordNew.requestFocus()
            return false
        }

        if (passwordConfirm.isEmpty()) {
            showToast("Подтвердите пароль")
            binding.editTextPasswordNewConfirm.requestFocus()
            return false
        }

        if (!isValidEmail(email)) {
            showToast("Введите корректный email")
            binding.editTextTextEmailAddress.requestFocus()
            return false
        }

        if (username.length < 3) {
            showToast("Имя пользователя должно содержать минимум 3 символа")
            binding.editTextUsernameNew.requestFocus()
            return false
        }

        if (password.length < 6) {
            showToast("Пароль должен содержать минимум 6 символов")
            binding.editTextPasswordNew.requestFocus()
            return false
        }

        if (password != passwordConfirm) {
            showToast("Пароли не совпадают")
            binding.editTextPasswordNewConfirm.requestFocus()
            return false
        }

        if (!isValidUsername(username)) {
            showToast("Имя пользователя должно содержать от 8 до 20 символов")
            binding.editTextUsernameNew.requestFocus()
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