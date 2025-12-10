package com.example.succulentus

import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.example.succulentus.databinding.ActivityMainBinding
import com.example.succulentus.databinding.FragmentLoginBinding

class MainActivity : LoggingActivity() {

    //TODO: биндинги
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun findHomeFragment(): HomeFragment? {
        supportFragmentManager.fragments.forEach { fragment ->
            if (fragment is HomeFragment) {
                return fragment
            }
            fragment.childFragmentManager?.fragments?.forEach { childFragment ->
                if (childFragment is HomeFragment) {
                    return childFragment
                }
            }
        }
        return null
    }

    suspend fun getCharacters(): List<Character> {
        return (findHomeFragment()?.getCharactersData() ?: emptyList()) as List<Character>
    }
}