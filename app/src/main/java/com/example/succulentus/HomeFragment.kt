package com.example.succulentus

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.succulentus.data.Character
import com.example.succulentus.database.CharacterDatabase
import com.example.succulentus.databinding.FragmentHomeBinding
import com.example.succulentus.network.KtorNetwork
import com.example.succulentus.network.KtorNetworkApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first

class HomeFragment : LoggingFragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var characterAdapter: CharacterAdapter
    private var _ktorApi: KtorNetworkApi? = null
    private val ktorApi get() = _ktorApi!!

    private var currentPage = 1
    private val itemsPerPage = 10
    private var isLoading = false
    private var hasMoreData = true
    private lateinit var database: CharacterDatabase

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        _ktorApi = KtorNetwork()

        // Инициализация базы данных
        database = CharacterDatabase.getDatabase(requireContext())

        binding.let { binding ->
            binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

            // Инициализация адаптера с пустым списком
            characterAdapter = CharacterAdapter(emptyList())
            binding.recyclerView.adapter = characterAdapter

            // Холодный старт: проверяем данные в БД
            checkDatabaseData()
        }


        observeCharactersFlow()

        return binding.root
    }

    private fun observeCharactersFlow() {
        lifecycleScope.launch {
            // Подписываемся на Flow из DAO
            database.characterDao().getAllFlow().collect { characters ->
                // Автоматически обновляем список при изменении данных в БД
                characterAdapter.updateData(characters)

                // Можно добавить логирование для отладки
                // Log.d("Flow", "Получено ${characters.size} персонажей")
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPref = requireContext().getSharedPreferences("app_settings",
            android.content.Context.MODE_PRIVATE)
        val username = sharedPref.getString("username", "User") ?: "User"
        binding.textViewUsername.text = username

        binding.imageButtonAccount.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToSettingsFragment()
            findNavController().navigate(action)
        }

        // Обработчик для кнопки обновления
        binding.refreshButton.setOnClickListener {
            refreshData()
        }

        // Добавляем слушатель прокрутки для пагинации
        binding.recyclerView.addOnScrollListener(object : androidx.recyclerview.widget.RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: androidx.recyclerview.widget.RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                if (!isLoading && hasMoreData) {
                    if (visibleItemCount + firstVisibleItemPosition >= totalItemCount
                        && firstVisibleItemPosition >= 0
                        && totalItemCount >= itemsPerPage) {
                        loadMoreData()
                    }
                }
            }
        })
    }

    private fun checkDatabaseData() {
        lifecycleScope.launch {
            try {
                // Получаем первое значение из Flow
                val charactersFromDb = database.characterDao().getAllFlow().first()

                if (charactersFromDb.isEmpty()) {
                    // Делаем запрос к API
                    loadCharactersFromApi()
                }
            } catch (e: Exception) {
                // В случае ошибки БД, загружаем из API
                loadCharactersFromApi()
            }
        }
    }

    private fun loadCharactersFromApi() {
        if (isLoading) return

        isLoading = true
        lifecycleScope.launch {
            try {
                val charactersFromApi = ktorApi.getCharacters(currentPage)

                charactersFromApi?.let { characters ->
                    if (characters.isNotEmpty()) {
                        // Сохраняем в БД - после этого Flow автоматически обновит список
                        database.characterDao().insertAll(characters)

                        // Сбрасываем пагинацию
                        currentPage = 1
                        hasMoreData = characters.size >= itemsPerPage

                        Toast.makeText(requireContext(), "Данные загружены", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "Нет данных для отображения", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Ошибка загрузки: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                isLoading = false
            }
        }
    }

    private fun refreshData() {
        if (isLoading) return

        isLoading = true
        currentPage = 1
        hasMoreData = true

        lifecycleScope.launch {
            try {
                // Очищаем БД перед обновлением
                database.characterDao().deleteAll()

                // Загружаем первую страницу
                val charactersFromApi = ktorApi.getCharacters(currentPage)

                charactersFromApi?.let { characters ->
                    if (characters.isNotEmpty()) {
                        database.characterDao().insertAll(characters)

                        hasMoreData = characters.size >= itemsPerPage

                        Toast.makeText(requireContext(), "Данные обновлены", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "Нет данных для обновления", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Ошибка обновления: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                isLoading = false
            }
        }
    }

    private fun loadMoreData() {
        if (isLoading || !hasMoreData) return

        isLoading = true
        currentPage++

        lifecycleScope.launch {
            try {
                // Загружаем следующую страницу
                val nextCharacters = ktorApi.getCharacters(currentPage)

                nextCharacters?.let { newCharacters ->
                    if (newCharacters.isNotEmpty()) {
                        database.characterDao().insertAll(newCharacters)

                        hasMoreData = newCharacters.size >= itemsPerPage

                        Toast.makeText(requireContext(), "Загружено еще ${newCharacters.size} элементов", Toast.LENGTH_SHORT).show()
                    } else {
                        hasMoreData = false
                        Toast.makeText(requireContext(), "Все данные загружены", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                currentPage-- // Откатываем страницу при ошибке
                Toast.makeText(requireContext(), "Ошибка загрузки: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                isLoading = false
            }
        }
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

    fun getCharactersData(): List<Character> {
        return try {
            characterAdapter.getCharacters() ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
}