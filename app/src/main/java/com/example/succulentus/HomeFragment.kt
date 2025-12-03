package com.example.succulentus

import LoggingFragment
import android.os.Bundle
import android.widget.TextView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs

class HomeFragment : LoggingFragment() {

    private lateinit var textViewUsername: TextView
    private val args: HomeFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        textViewUsername = view.findViewById(R.id.textViewUsername)
        // Получение имени пользователя через Safe Args
        textViewUsername.text = args.username
    }

    companion object {
        fun newInstance(username: String? = null): HomeFragment {
            val fragment = HomeFragment()
            return fragment
        }
    }
}