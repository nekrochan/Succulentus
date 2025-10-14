package com.example.succulentus

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SignUpActivity : AppCompatActivity() {

    // объявление полей для ввода данных
    private lateinit var editTextUsernameNew: EditText
    private lateinit var editTextTextEmailAddress: EditText
    private lateinit var editTextPasswordNew: EditText
    private lateinit var editTextPasswordNewConfirm: EditText

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_up)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // инициализация поля для ввода
        editTextUsernameNew = findViewById(R.id.editTextUsernameNew)
        editTextTextEmailAddress = findViewById(R.id.editTextTextEmailAddress)
        editTextPasswordNew = findViewById(R.id.editTextPasswordNew)
        editTextPasswordNewConfirm = findViewById(R.id.editTextPasswordNewConfirm)

        val buttonSigningUp = findViewById<Button>(R.id.buttonSigningUp)
        buttonSigningUp.setOnClickListener {
            // проверка корректности данных перед регистрацией
            if (validateSignUpData()) {
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("username", editTextUsernameNew.text.toString().trim())
                startActivity(intent)
                finish()
            }
        }

        val buttonHaveAccount = findViewById<Button>(R.id.buttonHaveAccount)
        buttonHaveAccount.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
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

        // проверка на специальные символы в имени пользователя
        if (!isValidUsername(username)) {
            showToast("Имя пользователя содержит недопустимые символы")
            editTextUsernameNew.requestFocus()
            return false
        }

        return true
    }

    /**
     * Функция для регистрации нового пользователя (заглушка для бд)
     * @return Boolean - true если регистрация успешна, false если пользователь уже существует
     */
    private fun registerNewUser(): Boolean {
        val username = editTextUsernameNew.text.toString().trim()
        val email = editTextTextEmailAddress.text.toString().trim()
        val password = editTextPasswordNew.text.toString().trim()

        // заглушка для проверки существования пользователя в бд
        if (checkIfUserExists(username, email)) {
            showToast("Пользователь с таким именем или email уже существует")
            return false
        }

        // заглушка для сохранения пользователя в бд
        if (saveUserToDatabase(username, email, password)) {
            showToast("Регистрация прошла успешно")
            return true
        } else {
            showToast("Ошибка регистрации. Попробуйте позже")
            return false
        }
    }

    /**
     * Заглушка для проверки существования пользователя
     * В реальном приложении здесь будет запрос к бд
     */
    private fun checkIfUserExists(username: String, email: String): Boolean {
        // Если пользователя еще нет в базе, возвращает false, тогда ошибка не вылезет
        return false
    }

    /**
     * Заглушка для сохранения пользователя в бд
     */
    private fun saveUserToDatabase(username: String, email: String, password: String): Boolean {
        return true
    }

    /**
     * Базовая проверка формата email
     */
    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    /**
     * Проверка имени пользователя на допустимые символы
     */
    private fun isValidUsername(username: String): Boolean {
        val usernameRegex = "^[a-zA-Z0-9_-]{3,20}$".toRegex()
        return username.matches(usernameRegex)
    }

    /**
     * Вспомогательная функция для показа уведомлений
     */
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}