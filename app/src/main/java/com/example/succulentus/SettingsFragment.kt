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
    private var externalBackupFile: File? = null  // Файл в загрузках
    private var internalBackupFile: File? = null  // Файл во внутреннем хранилище
    private var hasExternalBackup: Boolean = false
    private var hasInternalBackup: Boolean = false
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
        checkBackupFiles()

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
            showDeleteBackupDialog()
        }

        binding.buttonRestoreBackup.setOnClickListener {
            showRestoreBackupDialog()
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

                // Обновляем отображение
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
                checkBackupFiles()
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

        // Сохраняем юзерку в SharedPreferences
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
                    try {
                        val ktorApi = KtorNetwork()
                        ktorApi.getCharacters()
                    } catch (e: Exception) {
                        emptyList()
                    }
                }

                if (characters != null && characters.isEmpty()) {
                    Toast.makeText(requireContext(),
                        "Нет данных для резервного копирования", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                // Форматируем данные в текстовый формат
                val backupData = formatBackupData(characters)

                // Сохраняем ВО ВНЕШНЕЕ хранилище (загрузки)
                val success = saveBackupToExternalStorage(backupData)

                if (success) {
                    hasExternalBackup = true
                    updateFileInfo()
                    Toast.makeText(requireContext(),
                        "Резервная копия создана в загрузках (${characters?.size ?: 0} записей)",
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

    private fun deleteBackup() {
        try {
            // 1. Удаляем файл из загрузок
            if (externalBackupFile != null && externalBackupFile!!.exists()) {
                if (externalBackupFile!!.delete()) {
                    // 2. Сохраняем копию во внутреннем хранилище
                    val backupData = readBackupFile(externalBackupFile!!)
                    if (backupData != null) {
                        saveBackupToInternalStorage(backupData)
                    }

                    externalBackupFile = null
                    hasExternalBackup = false

                    // 3. Проверяем наличие внутренней копии
                    checkInternalBackupFile()

                    updateFileInfo()
                    Toast.makeText(requireContext(),
                        "Резервная копия удалена из загрузок и сохранена во внутреннем хранилище",
                        Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Ошибка удаления файла", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Файл не найден в загрузках", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Ошибка удаления файла: ${e.message}",
                Toast.LENGTH_SHORT).show()
            Log.e("SettingsFragment", "Error deleting backup", e)
        }
    }

    private fun restoreBackup() {
        try {
            // 1. Проверяем наличие внутренней резервной копии
            if (internalBackupFile != null && internalBackupFile!!.exists()) {
                // 2. Читаем данные из внутреннего хранилища
                val backupData = readBackupFile(internalBackupFile!!)
                if (backupData != null) {
                    // 3. Сохраняем в загрузки
                    val success = saveBackupToExternalStorage(backupData)

                    if (success) {
                        // 4. Удаляем из внутреннего хранилища
                        if (internalBackupFile!!.delete()) {
                            internalBackupFile = null
                            hasInternalBackup = false
                            hasExternalBackup = true

                            updateFileInfo()
                            Toast.makeText(requireContext(),
                                "Резервная копия восстановлена в загрузки и удалена из внутреннего хранилища",
                                Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(requireContext(), "Ошибка чтения резервной копии",
                        Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Резервная копия не найдена во внутреннем хранилище",
                    Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Ошибка восстановления файла: ${e.message}",
                Toast.LENGTH_SHORT).show()
            Log.e("SettingsFragment", "Error restoring backup", e)
        }
    }

    private fun readBackupFile(file: File): String? {
        return try {
            FileInputStream(file).bufferedReader().use { it.readText() }
        } catch (e: Exception) {
            Log.e("SettingsFragment", "Error reading backup file", e)
            null
        }
    }

    private fun formatBackupData(characters: List<Character>?): String {
        return buildString {
            appendLine("=== РЕЗЕРВНАЯ КОПИЯ ДАННЫХ ===")
            appendLine("Дата создания: ${SimpleDateFormat("dd.MM.yyyy HH:mm:ss",
                Locale.getDefault()).format(Date())}")
            appendLine("Всего записей: ${characters?.size ?: 0}")
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

            externalBackupFile = File(appDir, backupFileName)

            FileOutputStream(externalBackupFile).use { output ->
                output.write(data.toByteArray(Charsets.UTF_8))
            }

            true
        } catch (e: Exception) {
            Log.e("SettingsFragment", "Error saving to external storage", e)
            false
        }
    }

    private fun saveBackupToInternalStorage(data: String) {
        try {
            // Создаем папку для резервных копий
            val backupDir = File(requireContext().filesDir, "backups")
            if (!backupDir.exists()) {
                backupDir.mkdirs()
            }

            // Сохраняем с текущим именем файла
            internalBackupFile = File(backupDir, backupFileName)

            FileOutputStream(internalBackupFile).use { output ->
                output.write(data.toByteArray(Charsets.UTF_8))
            }

            Log.d("SettingsFragment", "Internal backup saved: ${internalBackupFile?.absolutePath}")
        } catch (e: Exception) {
            Log.e("SettingsFragment", "Error saving internal backup", e)
        }
    }

    private fun checkBackupFiles() {
        checkExternalBackupFile()
        checkInternalBackupFile()
    }

    private fun checkExternalBackupFile() {
        try {
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val appDir = File(downloadsDir, "SucculentusBackups")
            externalBackupFile = File(appDir, backupFileName)
            hasExternalBackup = externalBackupFile?.exists() ?: false
        } catch (e: Exception) {
            Log.e("SettingsFragment", "Error checking external backup file", e)
            hasExternalBackup = false
        }
    }

    private fun checkInternalBackupFile() {
        try {
            val backupDir = File(requireContext().filesDir, "backups")
            if (backupDir.exists()) {
                internalBackupFile = File(backupDir, backupFileName)
                hasInternalBackup = internalBackupFile?.exists() ?: false
            } else {
                hasInternalBackup = false
            }
        } catch (e: Exception) {
            Log.e("SettingsFragment", "Error checking internal backup file", e)
            hasInternalBackup = false
        }
    }

    private fun updateFileInfo() {
        val info = StringBuilder()

        // Информация о внешней резервной копии (загрузки)
        if (hasExternalBackup && externalBackupFile != null && externalBackupFile!!.exists()) {
            val file = externalBackupFile!!
            try {
                val sizeKB = file.length() / 1024
                val lastModified = SimpleDateFormat("dd.MM.yyyy HH:mm",
                    Locale.getDefault()).format(Date(file.lastModified()))

                info.appendLine("=== РЕЗЕРВНАЯ КОПИЯ В ЗАГРУЗКАХ ===")
                info.appendLine("Файл: ${file.name}")
                info.appendLine("Размер: ${sizeKB} KB")
                info.appendLine("Создан: $lastModified")
                info.appendLine("Путь: ${file.parent}")
                info.appendLine()
            } catch (e: Exception) {
                info.appendLine("Ошибка чтения информации о файле")
            }
        } else {
            info.appendLine("Резервная копия в загрузках: НЕ НАЙДЕНА")
            info.appendLine()
        }

        // Информация о внутренней резервной копии
        if (hasInternalBackup && internalBackupFile != null && internalBackupFile!!.exists()) {
            val file = internalBackupFile!!
            try {
                val sizeKB = file.length() / 1024
                val lastModified = SimpleDateFormat("dd.MM.yyyy HH:mm",
                    Locale.getDefault()).format(Date(file.lastModified()))

                info.appendLine("=== ВНУТРЕННЯЯ РЕЗЕРВНАЯ КОПИЯ ===")
                info.appendLine("Файл: ${file.name}")
                info.appendLine("Размер: ${sizeKB} KB")
                info.appendLine("Создан: $lastModified")
                info.appendLine("Путь: Внутреннее хранилище")
            } catch (e: Exception) {
                info.appendLine("Ошибка чтения информации о внутреннем файле")
            }
        } else {
            info.appendLine("Внутренняя резервная копия: НЕ НАЙДЕНА")
        }

        binding.textViewFileInfo.text = info.toString()

        // Обновляем состояние кнопок
        binding.buttonDeleteBackup.isEnabled = hasExternalBackup
        binding.buttonRestoreBackup.isEnabled = hasInternalBackup

        if (hasExternalBackup) {
            binding.buttonCreateBackup.text = "Обновить резервную копию"
        } else {
            binding.buttonCreateBackup.text = "Создать резервную копию"
        }
    }

    private fun showCreateBackupDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Создание резервной копии")
            .setMessage("Создать резервную копию данных в папке Загрузки?")
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

    private fun showDeleteBackupDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Удаление резервной копии")
            .setMessage("Удалить резервную копию из Загрузок и сохранить её во внутреннем хранилище?")
            .setPositiveButton("Удалить") { _, _ ->
                deleteBackup()
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun showRestoreBackupDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Восстановление резервной копии")
            .setMessage("Восстановить резервную копию из внутреннего хранилища в Загрузки?")
            .setPositiveButton("Восстановить") { _, _ ->
                restoreBackup()
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun formatBackupDataAsJson(characters: List<Character>): String {
        return Json { prettyPrint = true }.encodeToString(characters)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

interface CharacterDataProvider {
    suspend fun getCharacters(): List<Character>
}