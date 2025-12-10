package com.example.succulentus

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.succulentus.databinding.FragmentHomeBinding
import com.example.succulentus.network.KtorNetwork
import com.example.succulentus.network.KtorNetworkApi
import kotlinx.coroutines.launch

class HomeFragment : LoggingFragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val args: HomeFragmentArgs by navArgs()
    private lateinit var characterAdapter: CharacterAdapter
    private var _ktorApi: KtorNetworkApi? = null
    private val ktorApi get() = _ktorApi!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        //баннинг здесь
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        _ktorApi = KtorNetwork()

        binding.let { binding ->
            binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

            try{
                lifecycleScope.launch{
                    val charactersLists = ktorApi.getCharacters()
                    characterAdapter = CharacterAdapter(charactersLists)
                    binding.recyclerView.adapter=characterAdapter
                }
            }
            catch (e: Exception){
                Toast.makeText(requireContext(), "No Internet", Toast.LENGTH_SHORT).show()
            }
        }
        
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Получение имени пользователя через Safe Args
        //баннинг здесь и тд
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