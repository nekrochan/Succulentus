package com.example.succulentus

import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI

class MainActivity : LoggingActivity() {

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Находим NavHostFragment
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.fragmentContainer) as NavHostFragment
        navController = navHostFragment.navController

        // Настраиваем AppBar
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration)
    }

    /**
     * Обработка кнопки "Назад" в ActionBar
     */
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    /**
     * Обработка системной кнопки "Назад"
     */
    override fun onBackPressed() {
        // Если текущий фрагмент - SignUpFragment, выполняем навигацию назад
        if (navController.currentDestination?.id == R.id.signUpFragment) {
            navController.navigateUp()
        } else {
            // Для остальных фрагментов - закрываем приложение
            finishAffinity()
        }
    }
}