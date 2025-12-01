package com.example.succulentus

import LoggingActivity
import LoggingFragment
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment

class OnboardingFragment : LoggingFragment() {

    /*
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_onboarding)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val buttonGetStarted = findViewById<Button>(R.id.buttonGetStarted)
        buttonGetStarted.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()  // закрыть текущую активити
        }
    }

     */

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_onboarding, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val buttonGetStarted = view.findViewById<Button>(R.id.buttonGetStarted)
        buttonGetStarted.setOnClickListener {
            onGetStartedClickListener?.onGetStartedClick()
        }
    }

    // интерфейс для коммуникации с Activity
    interface OnGetStartedClickListener {
        fun onGetStartedClick()
    }

    var onGetStartedClickListener: OnGetStartedClickListener? = null

    /**
     * Метод для создания нового экземпляра фрагмента
     */
    companion object {
        fun newInstance(): OnboardingFragment {
            return OnboardingFragment()
        }
    }
}