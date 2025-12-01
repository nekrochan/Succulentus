package com.example.succulentus

import LoggingFragment
import User
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class SignUpFragment : LoggingFragment() {

    // объявление полей для ввода данных
    private lateinit var editTextUsernameNew: EditText
    private lateinit var editTextTextEmailAddress: EditText
    private lateinit var editTextPasswordNew: EditText
    private lateinit var editTextPasswordNewConfirm: EditText

    /*
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
                val user = User(
                    email = editTextTextEmailAddress.text.toString().trim(),
                    password = editTextPasswordNew.text.toString().trim(),
                    username = editTextUsernameNew.text.toString().trim()
                )
                // сохранение пользователя в бд с передачей данных в LoginActivity
                if (registerNewUser(user)) {
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.putExtra("user", user)
                    startActivity(intent)
                    finish()
                }
            }
        }

        val buttonHaveAccount = findViewById<Button>(R.id.buttonHaveAccount)
        buttonHaveAccount.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

     */

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sign_up, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Инициализация полей для ввода
        editTextUsernameNew = view.findViewById(R.id.editTextUsernameNew)
        editTextTextEmailAddress = view.findViewById(R.id.editTextTextEmailAddress)
        editTextPasswordNew = view.findViewById(R.id.editTextPasswordNew)
        editTextPasswordNewConfirm = view.findViewById(R.id.editTextPasswordNewConfirm)

        val buttonSigningUp = view.findViewById<Button>(R.id.buttonSigningUp)
        buttonSigningUp.setOnClickListener {
            // Проверка корректности данных перед регистрацией
            if (validateSignUpData()) {
                val user = User(
                    email = editTextTextEmailAddress.text.toString().trim(),
                    password = editTextPasswordNew.text.toString().trim(),
                    username = editTextUsernameNew.text.toString().trim()
                )
                // Сохранение пользователя в БД и передача данных в LoginFragment
                if (registerNewUser(user)) {
                    onRegistrationCompleteListener?.onRegistrationComplete(user)
                }
            }
        }

        val buttonHaveAccount = view.findViewById<Button>(R.id.buttonHaveAccount)
        buttonHaveAccount.setOnClickListener {
            onHaveAccountClickListener?.onHaveAccountClick()
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
        // заглушка для проверки существования пользователя в бд
        if (checkIfUserExists(user.username, user.email)) {
            showToast("Пользователь с таким именем или email уже существует")
            return false
        }

        // заглушка для сохранения пользователя в бд
        if (saveUserToDatabase(user)) {
            showToast("Регистрация прошла успешно")
            return true
        } else {
            showToast("Ошибка регистрации. Попробуйте позже")
            return false
        }
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
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
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


    // ДОБАВИЛА:

    // интерфейсы для коммуникации с Activity
    interface OnRegistrationCompleteListener {
        fun onRegistrationComplete(user: User)
    }
    var onRegistrationCompleteListener: OnRegistrationCompleteListener? = null

    interface OnHaveAccountClickListener {
        fun onHaveAccountClick()
    }
    var onHaveAccountClickListener: OnHaveAccountClickListener? = null

    /**
     * Метод для создания нового экземпляра фрагмента
     */
    companion object {
        fun newInstance(): SignUpFragment {
            return SignUpFragment()
        }
    }
}