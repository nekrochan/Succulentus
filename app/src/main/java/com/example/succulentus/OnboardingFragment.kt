package com.example.succulentus

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.succulentus.databinding.FragmentOnboardingBinding

class OnboardingFragment : LoggingFragment() {

    private var _binding: FragmentOnboardingBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //баннинг здесь
        _binding = FragmentOnboardingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //баннинг здесь
        binding.buttonGetStarted.setOnClickListener {
            // Навигация к LoginFragment с помощью Navigation Component
            findNavController().navigate(R.id.action_onboardingFragment_to_loginFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        //баннинг здесь
        _binding = null
    }

    companion object {
        fun newInstance(): OnboardingFragment {
            return OnboardingFragment()
        }
    }
}