package com.example.succulentus

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class LoginActivity : AppCompatActivity() {

    // обновление полей для ввода данных
    private lateinit var editTextUsername: EditText
    private lateinit var editTextPassword: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // инициализация поля для ввода (предполагая, что они есть в layout)
        editTextUsername = findViewById(R.id.editTextUsername)
        editTextPassword = findViewById(R.id.editTextPassword)

        val buttonSignIn = findViewById<Button>(R.id.buttonSignedIn)
        buttonSignIn.setOnClickListener {
            if (validateLoginData()) {
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("username", editTextUsername.text.toString().trim())
                startActivity(intent)
                finish()
            }
        }

        val buttonSignUp = findViewById<Button>(R.id.buttonSignUp)
        buttonSignUp.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
            finish()
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
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    /**
     * Дополнительная функция для проверки наличия данных пользователя в базе данных
     * (раскомментируйте и реализуйте при необходимости)
     */
    private fun checkUserInDatabase(username: String, password: String): Boolean {
        return true
    }

}