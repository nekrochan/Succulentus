package com.example.succulentus

import LoggingFragment
import User
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController

class SignUpFragment : LoggingFragment() {

    // объявление полей для ввода данных
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

        // Инициализация полей
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
                    // Навигация обратно к LoginFragment с передачей user
                    // и удалением SignUpFragment из стека
                    findNavController().navigate(
                        SignUpFragmentDirections.actionSignUpFragmentToLoginFragment(user.username)
                    )
                }
            }
        }

        val buttonHaveAccount = view.findViewById<Button>(R.id.buttonHaveAccount)
        buttonHaveAccount.setOnClickListener {
            // Навигация обратно к LoginFragment (просто назад)
            findNavController().navigateUp()
        }
    }

    /**
     * Функция для проверки корректности введенных данных при регистрации
     * @return Boolean - true если данные корректны, false если есть ошибки
     */
    private fun validateSignUpData(): Boolean {
        val username = editTextUsernameNew.text.toString().trim()
        val email = editTextTextEmailAddress.text.toString().trim()
        val password = editTextPasswordNew.text.toString().trim()
        val passwordConfirm = editTextPasswordNewConfirm.text.toString().trim()

        // проверка на пустые поля
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

        // проверка формата email
        if (!isValidEmail(email)) {
            showToast("Введите корректный email")
            editTextTextEmailAddress.requestFocus()
            return false
        }

        // проверка длины имени пользователя
        if (username.length < 3) {
            showToast("Имя пользователя должно содержать минимум 3 символа")
            editTextUsernameNew.requestFocus()
            return false
        }

        // проверка длины пароля
        if (password.length < 6) {
            showToast("Пароль должен содержать минимум 6 символов")
            editTextPasswordNew.requestFocus()
            return false
        }

        // проверка совпадения паролей
        if (password != passwordConfirm) {
            showToast("Пароли не совпадают")
            editTextPasswordNewConfirm.requestFocus()
            return false
        }

        // проверка юзерки
        if (!isValidUsername(username)) {
            showToast("Имя пользователя должно содержать от 8 до 20 символов")
            editTextUsernameNew.requestFocus()
            return false
        }

        return true
    }

    /**
     * Функция для регистрации нового пользователя (заглушка для бд)
     * @return Boolean - true если регистрация успешна, false если пользователь уже существует
     */
    private fun registerNewUser(user: User): Boolean {
        if (checkIfUserExists(user.username, user.email)) {
            showToast("Пользователь с таким именем или email уже существует")
            return false
        }

        if (saveUserToDatabase(user)) {
            // Передаем объект User
            val action = SignUpFragmentDirections.actionSignUpFragmentToLoginFragment(user)
            findNavController().navigate(action)
            return true
        }

        return false
    }

    /**
     * Заглушка для проверки существования пользователя
     */
    private fun checkIfUserExists(username: String, email: String): Boolean {
        // Если пользователя еще нет в базе, возвращает false, тогда ошибка не вылезет
        return false
    }

    /**
     * Заглушка для сохранения пользователя в бд
     */
    private fun saveUserToDatabase(user: User): Boolean {
        return true
    }

    /**
     * Базовая проверка формата email
     */
    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    /**
     * Проверка имени пользователя на допустимые символы
     */
    private fun isValidUsername(username: String): Boolean {
        return username.length in 8..20
    }

    /**
     * Вспомогательная функция для показа уведомлений
     */
    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}