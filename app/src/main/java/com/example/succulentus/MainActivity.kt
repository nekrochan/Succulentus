package com.example.succulentus

import LoggingActivity
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.TextView

class MainActivity : LoggingActivity() {

    private lateinit var textViewUsername: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // поиск TextView для имени пользователя
        textViewUsername = findViewById(R.id.textViewUsername)

        // получение имя пользователя из Intent
        val username = getUsernameFromIntent()

        // передача полученной юзерки в TextView
        textViewUsername.text = username
    }

    /**
     * Получает имя пользователя из Intent
     * Если имя не передано, использует значение по умолчанию
     */
    private fun getUsernameFromIntent(): String {
        return intent.getStringExtra("username") ?: "Гость"
    }

}