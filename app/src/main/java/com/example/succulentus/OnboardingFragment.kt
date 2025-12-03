package com.example.succulentus

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController

class OnboardingFragment : LoggingFragment() {

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
            // Навигация к LoginFragment с помощью Navigation Component
            findNavController().navigate(R.id.action_onboardingFragment_to_loginFragment)
        }
    }

    companion object {
        fun newInstance(): OnboardingFragment {
            return OnboardingFragment()
        }
    }
}