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

    // Получение аргументов через Safe Args
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

        // Инициализация полей
        editTextUsername = view.findViewById(R.id.editTextUsername)
        editTextPassword = view.findViewById(R.id.editTextPassword)

        // Получение данных из аргументов
        val prefilledUsername = args.prefilledUsername
        val prefilledPassword = args.prefilledPassword

        if (prefilledUsername.isNotEmpty()) {
            editTextUsername.setText(prefilledUsername)
        }
        if (prefilledPassword.isNotEmpty()) {
            editTextPassword.setText(prefilledPassword)
        }

        // Получение данных пользователя из аргументов (если перешли из SignUpFragment)
        val user = args.user
        user?.let {
            editTextUsername.setText(prefilledUsername)
            editTextPassword.setText(prefilledPassword)
        }

        val buttonSignIn = view.findViewById<Button>(R.id.buttonSignedIn)
        buttonSignIn.setOnClickListener {
            if (validateLoginData()) {
                val username = editTextUsername.text.toString().trim()
                // Навигация к HomeFragment с очисткой стека
                findNavController().navigate(
                    LoginFragmentDirections.actionLoginFragmentToHomeFragment(username)
                )
            }
        }

        val buttonSignUp = view.findViewById<Button>(R.id.buttonSignUp)
        buttonSignUp.setOnClickListener {
            // Навигация к SignUpFragment (добавляется в back stack)
            findNavController().navigate(
                LoginFragmentDirections.actionLoginFragmentToSignUpFragment()
            )
        }
    }

    /**
     * Функция для проверки корректности введенных данных
     * @return Boolean - true если данные корректны, false если есть ошибки
     */
    private fun validateLoginData(): Boolean {
        val username = editTextUsername.text.toString().trim()
        val password = editTextPassword.text.toString().trim()

        // проверка на пустые поля
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

        // проверка формата username (базовая проверка)
        if (!isValidUsername(username)) {
            showToast("Введите корректный username")
            editTextUsername.requestFocus()
            return false
        }

        // проверка длины пароля
        if (password.length < 5) {
            showToast("Пароль должен содержать минимум 5 символов")
            editTextPassword.requestFocus()
            return false
        }

        // здесь будет сверка данных с бд
        if (!checkUserInDatabase(username, password)) {
            showToast("Неверный username или пароль")
            return false
        }

        return true
    }

    /**
     * Базовая проверка формата username
     */
    private fun isValidUsername(username: String): Boolean {
        return true
    }

    /**
     * Вспомогательная функция для показа уведомлений
     */
    private fun showToast(message: String) {
        // Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    /**
     * Дополнительная функция для проверки наличия данных пользователя в базе данных
     */
    private fun checkUserInDatabase(username: String, password: String): Boolean {
        return true
    }

}