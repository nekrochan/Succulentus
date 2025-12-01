package com.example.succulentus

import LoggingFragment
import android.os.Bundle
import android.widget.TextView
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class HomeFragment : LoggingFragment() {

    private lateinit var textViewUsername: TextView

    /*
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)
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

     */

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // инициализация TextView
        textViewUsername = view.findViewById(R.id.textViewUsername)

        // получение имени пользователя из аргументов
        val username = getUsernameFromArguments()

        // установка имени пользователя в TextView
        textViewUsername.text = username
    }

    /**
     * Метод для создания нового экземпляра фрагмента с аргументами
     */
    companion object {
        fun newInstance(username: String? = null): HomeFragment {
            val fragment = HomeFragment()
            val args = Bundle()
            args.putString("username", username)
            fragment.arguments = args
            return fragment
        }
    }

    /**
     * Получает имя пользователя
     * Если имя не передано, использует значение по умолчанию
     */
    private fun getUsernameFromArguments(): String {
    // private fun getUsernameFromIntent(): String {
        // return intent.getStringExtra("username") ?: "Гость"
        return arguments?.getString("username") ?: "Гость"
    }

}