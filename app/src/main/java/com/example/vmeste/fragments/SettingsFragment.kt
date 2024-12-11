package com.example.vmeste.fragments

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.vmeste.databinding.FragmentSettingsBinding
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

private val Context.dataStore by preferencesDataStore(name = "settings")

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding ?: throw IllegalStateException("Binding is null")

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPreferences = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

        binding.saveButton.setOnClickListener {
            saveSettings()
        }

        binding.deleteFileButton.setOnClickListener {
            deleteFile()
        }

        binding.restoreFileButton.setOnClickListener {
            restoreFile()
        }

        loadSettings()
        checkFilePresence()
        checkBackupFilePresence()
    }

    private fun saveSettings() {
        val email = binding.emailEditText.text.toString()
        val theme = binding.themeSpinner.selectedItem.toString()
        val fontSize = binding.fontSizeSeekBar.progress.toFloat()

        saveEmail(email)
        saveTheme(theme)
        saveFontSize(fontSize)
        applyTheme(theme)
        applyFontSize(fontSize)
    }

    private fun loadSettings() {
        lifecycleScope.launch {
            binding.emailEditText.setText(getEmail())
        }
        val theme = getTheme()
        val fontSize = getFontSize()
        binding.themeSpinner.setSelection(getThemeIndex(theme))
        binding.fontSizeSeekBar.progress = fontSize.toInt()
    }

    private fun saveEmail(email: String) {
        lifecycleScope.launch {
            requireContext().dataStore.edit { preferences ->
                preferences[stringPreferencesKey("email")] = email
            }
        }
    }

    private suspend fun getEmail(): String? {
        val preferences = requireContext().dataStore.data.first()
        return preferences[stringPreferencesKey("email")]
    }

    private fun saveTheme(theme: String) {
        sharedPreferences.edit().putString("theme", theme).apply()
    }

    private fun getTheme(): String? {
        return sharedPreferences.getString("theme", "System")
    }

    private fun saveFontSize(fontSize: Float) {
        sharedPreferences.edit().putFloat("font_size", fontSize).apply()
    }

    private fun getFontSize(): Float {
        return sharedPreferences.getFloat("font_size", 16f)
    }

    private fun getThemeIndex(theme: String?): Int {
        return when (theme) {
            "Light" -> 0
            "Dark" -> 1
            else -> 2
        }
    }

    private fun applyTheme(theme: String) {
        when (theme) {
            "Light" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            "Dark" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
    }

    private fun applyFontSize(fontSize: Float) {
        saveFontSize(fontSize)
        requireActivity().recreate()
    }

    private fun checkFilePresence() {
        val fileName = "MayorGromVmeste.txt"
        val documentsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
        val file = File(documentsDir, fileName)

        if (file.exists()) {
            binding.fileInfoTextView.text = "Файл найден: $fileName"
            binding.deleteFileButton.visibility = View.VISIBLE
        } else {
            binding.fileInfoTextView.text = "Файл не найден"
            binding.deleteFileButton.visibility = View.GONE
        }
    }

    private fun checkBackupFilePresence() {
        val fileName = "MayorGromVmeste.txt"
        val backupFile = File(requireContext().filesDir, fileName)

        if (backupFile.exists()) {
            binding.restoreFileButton.visibility = View.VISIBLE
        } else {
            binding.restoreFileButton.visibility = View.GONE
        }
    }

    private fun deleteFile() {
        val fileName = "MayorGromVmeste.txt"
        val documentsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
        val file = File(documentsDir, fileName)

        if (file.exists()) {
            backupFile(file)
            if (file.delete()) {
                binding.fileInfoTextView.text = "Файл удален: $fileName"
                binding.deleteFileButton.visibility = View.GONE
                binding.restoreFileButton.visibility = View.VISIBLE
            } else {
                binding.fileInfoTextView.text = "Ошибка при удалении файла"
            }
        }
    }

    private fun backupFile(file: File) {
        val backupFile = File(requireContext().filesDir, file.name)
        FileInputStream(file).use { input ->
            FileOutputStream(backupFile).use { output ->
                input.copyTo(output)
            }
        }
    }

    private fun restoreFile() {
        val fileName = "MayorGromVmeste.txt"
        val backupFile = File(requireContext().filesDir, fileName)
        val documentsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
        val restoredFile = File(documentsDir, fileName)

        if (backupFile.exists()) {
            FileInputStream(backupFile).use { input ->
                FileOutputStream(restoredFile).use { output ->
                    input.copyTo(output)
                }
            }
            binding.fileInfoTextView.text = "Файл восстановлен: $fileName"
            binding.deleteFileButton.visibility = View.VISIBLE
            binding.restoreFileButton.visibility = View.GONE
        } else {
            binding.fileInfoTextView.text = "Ошибка при восстановлении файла"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
