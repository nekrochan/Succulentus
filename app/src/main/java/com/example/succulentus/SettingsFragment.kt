package com.example.succulentus

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.succulentus.data.Character
import com.example.succulentus.databinding.SettingsFragmentBinding
import com.example.succulentus.network.KtorNetwork
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.util.Log

class SettingsFragment : Fragment() {
    private var _binding: SettingsFragmentBinding? = null
    private val binding get() = _binding!!

    // Настройки по умолчанию
    private var username: String = "User"
    private var notificationsEnabled: Boolean = true
    private var language: String = "ru"
    private var fontSize: Int = 2
    private var backupFileName: String = "backup_01.txt"

    // Для работы с файлами
    private val REQUEST_PERMISSION_CODE = 100
    private var backupFile: File? = null
    private var hasBackup: Boolean = false
    private lateinit var dataStoreManager: DataStoreManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataStoreManager = DataStoreManager(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = SettingsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
        loadSettings()
        checkBackupFile()

        binding.seekBarFontSize.setOnSeekBarChangeListener(object :
            android.widget.SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: android.widget.SeekBar?, progress: Int, fromUser: Boolean) {
                fontSize = progress
                updateFontSizePreview()
            }
            override fun onStartTrackingTouch(seekBar: android.widget.SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: android.widget.SeekBar?) {}
        })

        binding.buttonDeleteBackup.setOnClickListener {
            deleteBackup()
        }

        binding.buttonRestoreBackup.setOnClickListener {
            restoreBackup()
        }

        binding.buttonSaveSettings.setOnClickListener {
            saveSettings()
        }

        binding.buttonCreateBackup.setOnClickListener {
            showCreateBackupDialog()
        }
    }

    private fun setupUI() {
        // Настройка Spinner для выбора языка
        val languages = arrayOf("Русский", "English", "Español")
        val adapter = ArrayAdapter(requireContext(),
            android.R.layout.simple_spinner_item, languages)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerLanguage.adapter = adapter
    }

    private fun loadSettings() {
        // Загружаем из SharedPreferences (username)
        val sharedPref = requireContext().getSharedPreferences("app_settings",
            android.content.Context.MODE_PRIVATE)
        username = sharedPref.getString("username", "User") ?: "User"
        binding.editTextUsername.setText(username)

        // Загружаем из DataStore
        lifecycleScope.launch {
            dataStoreManager.appSettings.collect { settings ->
                notificationsEnabled = settings.notificationsEnabled
                language = settings.language
                fontSize = settings.fontSize
                backupFileName = settings.backupFilename

                // Обновляем UI
                binding.switchNotifications.isChecked = notificationsEnabled

                val position = when(language) {
                    "en" -> 1
                    "es" -> 2
                    else -> 0
                }
                binding.spinnerLanguage.setSelection(position)

                binding.seekBarFontSize.progress = fontSize
                updateFontSizePreview()

                binding.editTextBackupFileName.setText(backupFileName)
                checkBackupFile()
            }
        }
    }

    private fun saveSettings() {
        val newUsername = binding.editTextUsername.text.toString().trim()
        val newNotificationsEnabled = binding.switchNotifications.isChecked
        val newLanguage = when(binding.spinnerLanguage.selectedItemPosition) {
            0 -> "ru"
            1 -> "en"
            2 -> "es"
            else -> "ru"
        }
        val newBackupFileName = binding.editTextBackupFileName.text.toString().trim()

        // Сохраняем username в SharedPreferences
        val sharedPref = requireContext().getSharedPreferences("app_settings",
            android.content.Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("username", newUsername)
            apply()
        }

        // Сохраняем в DataStore
        lifecycleScope.launch {
            dataStoreManager.saveSettings(
                AppSettings(
                    notificationsEnabled = newNotificationsEnabled,
                    language = newLanguage,
                    fontSize = fontSize,
                    backupFilename = if (newBackupFileName.isNotEmpty()) newBackupFileName else "backup_01.txt"
                )
            )
        }

        username = newUsername
        notificationsEnabled = newNotificationsEnabled
        language = newLanguage
        backupFileName = if (newBackupFileName.isNotEmpty()) newBackupFileName else "backup_01.txt"

        Toast.makeText(requireContext(), "Настройки сохранены", Toast.LENGTH_SHORT).show()
    }

    private fun updateFontSizePreview() {
        val sizes = arrayOf(12f, 14f, 16f, 18f, 20f)
        binding.textViewFontSizePreview.textSize = sizes.getOrElse(fontSize) { 16f }
    }

    private fun checkPermissions(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(requireContext(),
                        Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                intent.addCategory("android.intent.category.DEFAULT")
                intent.data = Uri.parse("package:${requireContext().packageName}")
                startActivity(intent)
            } catch (e: Exception) {
                val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                startActivity(intent)
            }
        } else {
            ActivityCompat.requestPermissions(requireActivity(),
                arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ),
                REQUEST_PERMISSION_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                createBackup()
            } else {
                Toast.makeText(requireContext(), "Разрешения не предоставлены", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun createBackup() {
        lifecycleScope.launch {
            try {
                // Получаем данные из приложения
                val characters = if (activity is CharacterDataProvider) {
                    (activity as CharacterDataProvider).getCharacters()
                } else {
                    // Альтернативный способ: создаем новый экземпляр KtorNetwork
                    try {
                        val ktorApi = KtorNetwork()
                        ktorApi.getCharacters()
                    } catch (e: Exception) {
                        emptyList()
                    }
                }

                if (characters != null) {
                    if (characters.isEmpty()) {
                        Toast.makeText(requireContext(),
                            "Нет данных для резервного копирования", Toast.LENGTH_SHORT).show()
                        return@launch
                    }
                }

                // Форматируем данные в текстовый формат
                val backupData = formatBackupData(characters)

                // Сохраняем во внешнее хранилище
                val success = saveBackupToExternalStorage(backupData)

                if (success) {
                    hasBackup = true
                    updateFileInfo()
                    Toast.makeText(requireContext(),
                        "Резервная копия создана (${characters?.size} записей)",
                        Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(),
                    "Ошибка создания резервной копии: ${e.message}",
                    Toast.LENGTH_SHORT).show()
                Log.e("SettingsFragment", "Error creating backup", e)
            }
        }
    }

    private fun formatBackupData(characters: List<Character>?): String {
        return buildString {
            appendLine("=== РЕЗЕРВНАЯ КОПИЯ ДАННЫХ ===")
            appendLine("Дата создания: ${SimpleDateFormat("dd.MM.yyyy HH:mm:ss",
                Locale.getDefault()).format(Date())}")
            appendLine("Всего записей: ${characters?.size}")
            appendLine("=".repeat(50))
            appendLine()

            characters?.forEachIndexed { index, character ->
                appendLine("Запись #${index + 1}")
                appendLine("ID: ${character._id?.toString() ?: "N/A"}")
                appendLine("Имя: ${character.name ?: "Не указано"}")
                appendLine("URL: ${character.url ?: "N/A"}")
                appendLine("Изображение: ${character.imageUrl ?: "N/A"}")
                appendLine("Выравнивание: ${character.alignment ?: "N/A"}")

                // Списки
                append("Фильмы: ")
                character.films?.let {
                    if (it.isNotEmpty()) append(it.joinToString(", "))
                    else append("Нет")
                } ?: append("Нет")
                appendLine()

                append("Короткометражки: ")
                character.shortFilms?.let {
                    if (it.isNotEmpty()) append(it.joinToString(", "))
                    else append("Нет")
                } ?: append("Нет")
                appendLine()

                append("ТВ-шоу: ")
                character.tvShows?.let {
                    if (it.isNotEmpty()) append(it.joinToString(", "))
                    else append("Нет")
                } ?: append("Нет")
                appendLine()

                append("Видеоигры: ")
                character.videoGames?.let {
                    if (it.isNotEmpty()) append(it.joinToString(", "))
                    else append("Нет")
                } ?: append("Нет")
                appendLine()

                append("Аттракционы парков: ")
                character.parkAttractions?.let {
                    if (it.isNotEmpty()) append(it.joinToString(", "))
                    else append("Нет")
                } ?: append("Нет")
                appendLine()

                append("Союзники: ")
                character.allies?.let {
                    if (it.isNotEmpty()) append(it.joinToString(", "))
                    else append("Нет")
                } ?: append("Нет")
                appendLine()

                append("Враги: ")
                character.enemies?.let {
                    if (it.isNotEmpty()) append(it.joinToString(", "))
                    else append("Нет")
                } ?: append("Нет")
                appendLine()

                appendLine("-".repeat(40))
                appendLine()
            }

            appendLine("=== КОНЕЦ РЕЗЕРВНОЙ КОПИИ ===")
        }
    }

    private fun saveBackupToExternalStorage(data: String): Boolean {
        return try {
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

            // Создаем папку с именем приложения
            val appDir = File(downloadsDir, "SucculentusBackups")
            if (!appDir.exists()) {
                appDir.mkdirs()
            }

            backupFile = File(appDir, backupFileName)

            FileOutputStream(backupFile).use { output ->
                output.write(data.toByteArray(Charsets.UTF_8))
            }

            true
        } catch (e: Exception) {
            Log.e("SettingsFragment", "Error saving to external storage", e)
            false
        }
    }

    private fun deleteBackup() {
        backupFile?.let { file ->
            if (file.exists()) {
                // Сначала создаем скрытую копию во внутреннем хранилище
                try {
                    FileInputStream(file).use { input ->
                        val internalBackup = File(requireContext().filesDir,
                            ".hidden_backup_${System.currentTimeMillis()}.txt")
                        FileOutputStream(internalBackup).use { output ->
                            input.copyTo(output)
                        }
                    }

                    // Удаляем основной файл
                    if (file.delete()) {
                        backupFile = null
                        hasBackup = false
                        updateFileInfo()
                        Toast.makeText(requireContext(), "Файл удален (резервная копия сохранена)",
                            Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Ошибка удаления файла",
                        Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun restoreBackup() {
        // Ищем скрытые резервные копии
        val internalDir = requireContext().filesDir
        val backupFiles = internalDir.listFiles { file ->
            file.name.startsWith(".hidden_backup_")
        }

        backupFiles?.maxByOrNull { it.lastModified() }?.let { latestBackup ->
            try {
                val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

                val appDir = File(downloadsDir, "SucculentusBackups")
                if (!appDir.exists()) {
                    appDir.mkdirs()
                }

                val restoredFile = File(appDir, backupFileName)

                FileInputStream(latestBackup).use { input ->
                    FileOutputStream(restoredFile).use { output ->
                        input.copyTo(output)
                    }
                }

                backupFile = restoredFile
                hasBackup = true
                latestBackup.delete()
                updateFileInfo()
                Toast.makeText(requireContext(), "Резервная копия восстановлена",
                    Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Ошибка восстановления",
                    Toast.LENGTH_SHORT).show()
            }
        } ?: run {
            Toast.makeText(requireContext(), "Резервная копия не найдена",
                Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkBackupFile() {
        try {
            val downloadsDir = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                requireContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
            } else {
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            }

            val appDir = File(downloadsDir, "SucculentusBackups")
            backupFile = File(appDir, backupFileName)

            hasBackup = backupFile?.exists() ?: false
            updateFileInfo()
        } catch (e: Exception) {
            Log.e("SettingsFragment", "Error checking backup file", e)
            hasBackup = false
            updateFileInfo()
        }
    }

    private fun updateFileInfo() {
        if (hasBackup && backupFile != null && backupFile!!.exists()) {
            val file = backupFile!!
            try {
                val sizeKB = file.length() / 1024
                val lastModified = SimpleDateFormat("dd.MM.yyyy HH:mm",
                    Locale.getDefault()).format(Date(file.lastModified()))

                // Читаем первую строку для информации о содержимом
                val firstLine = FileInputStream(file).bufferedReader().use { it.readLine() }
                val recordCount = FileInputStream(file).bufferedReader().use { reader ->
                    reader.lineSequence()
                        .firstOrNull { it.contains("Всего записей:") }
                        ?.substringAfter(": ")?.trim() ?: "N/A"
                }

                val info = """
                Файл: ${file.name}
                Размер: ${sizeKB} KB
                Записей: $recordCount
                Создан: $lastModified
                Путь: ${file.parent}
                Тип: $firstLine
            """.trimIndent()

                binding.textViewFileInfo.text = info
                binding.buttonDeleteBackup.isEnabled = true
                binding.buttonCreateBackup.text = "Обновить резервную копию"
            } catch (e: Exception) {
                binding.textViewFileInfo.text = "Ошибка чтения файла"
                binding.buttonDeleteBackup.isEnabled = true
            }
        } else {
            binding.textViewFileInfo.text = """
            Файл резервной копии не найден.
            
            Для создания резервной копии:
            1. Убедитесь, что загружены данные
            2. Нажмите "Создать резервную копию"
            3. При необходимости предоставьте разрешения
            
            Файл будет сохранен в:
            /Downloads/SucculentusBackups/
        """.trimIndent()
            binding.buttonDeleteBackup.isEnabled = false
            binding.buttonCreateBackup.text = "Создать резервную копию"
        }

        // Проверяем наличие внутренних резервных копий
        checkInternalBackups()
    }

    private fun checkInternalBackups() {
        val backupFiles = requireContext().filesDir.listFiles { file ->
            file.name.startsWith(".hidden_backup_") && file.name.endsWith(".txt")
        }

        val hasInternalBackup = backupFiles?.isNotEmpty() ?: false
        binding.buttonRestoreBackup.isEnabled = hasInternalBackup

        if (hasInternalBackup) {
            val latestBackup = backupFiles!!.maxByOrNull { it.lastModified() }
            val backupCount = backupFiles.size
            val latestDate = SimpleDateFormat("dd.MM.yyyy HH:mm",
                Locale.getDefault()).format(Date(latestBackup!!.lastModified()))

            binding.textViewFileInfo.append("\n\nДоступно внутренних резервных копий: $backupCount")
            binding.textViewFileInfo.append("\nПоследняя: $latestDate")
        }
    }

    private fun showCreateBackupDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Создание резервной копии")
            .setMessage("Вы уверены, что хотите создать резервную копию данных?")
            .setPositiveButton("Создать") { _, _ ->
                if (checkPermissions()) {
                    createBackup()
                } else {
                    requestPermissions()
                }
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

interface CharacterDataProvider {
    suspend fun getCharacters(): List<Character>
}