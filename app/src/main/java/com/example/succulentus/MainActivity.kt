package com.example.succulentus

import android.os.Bundle
import com.example.succulentus.databinding.ActivityMainBinding

class MainActivity : LoggingActivity() {

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

}