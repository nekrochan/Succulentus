package com.example.succulentus

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.example.succulentus.databinding.FragmentHomeBinding

class HomeFragment : LoggingFragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val args: HomeFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Получение имени пользователя через Safe Args
        binding.textViewUsername.text = args.username
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(username: String? = null): HomeFragment {
            val fragment = HomeFragment()
            return fragment
        }
    }
}