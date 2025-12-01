package com.example.succulentus

import LoggingActivity
import User
import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity

import android.os.Bundle
import androidx.fragment.app.Fragment

class MainActivity : LoggingActivity(),
    OnboardingFragment.OnGetStartedClickListener,
    LoginFragment.OnLoginSuccessListener,
    LoginFragment.OnSignUpClickListener,
    SignUpFragment.OnRegistrationCompleteListener,
    SignUpFragment.OnHaveAccountClickListener {

    private val fragmentContainerId = R.id.fragmentContainer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            showOnboardingFragment()
        }
    }

    private fun showOnboardingFragment() {
        val onboardingFragment = OnboardingFragment.newInstance()
        onboardingFragment.onGetStartedClickListener = this
        replaceFragment(onboardingFragment, addToBackStack = false)
    }

    private fun showLoginFragment(user: User? = null) {
        val loginFragment = LoginFragment.newInstance(user)
        loginFragment.onLoginSuccessListener = this
        loginFragment.onSignUpClickListener = this
        replaceFragment(loginFragment, addToBackStack = false)
    }

    private fun showSignUpFragment() {
        val signUpFragment = SignUpFragment.newInstance()
        signUpFragment.onRegistrationCompleteListener = this
        signUpFragment.onHaveAccountClickListener = this
        replaceFragment(signUpFragment, addToBackStack = true)
    }

    private fun showHomeFragment(username: String) {
        val homeFragment = HomeFragment.newInstance(username)
        replaceFragment(homeFragment, addToBackStack = false)
        clearBackStack()
    }

    /**
     * Заменяет фрагмент в контейнере
     * @param fragment - фрагмент для отображения
     * @param addToBackStack - добавлять ли в стек возврата
     */
    private fun replaceFragment(fragment: Fragment, addToBackStack: Boolean) {
        val transaction = supportFragmentManager.beginTransaction()

        // заменяем фрагмент в контейнере
        transaction.replace(fragmentContainerId, fragment)

        // добавляем в back stack если нужно
        if (addToBackStack) {
            transaction.addToBackStack(fragment::class.java.simpleName)
        }

        transaction.commit()
    }

    /**
     * Очищает стек возврата фрагментов
     */
    private fun clearBackStack() {
        // очищаем весь back stack
        supportFragmentManager.popBackStack(null, androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }

    // ========== Реализация интерфейсов фрагментов ==========

    override fun onGetStartedClick() {
        showLoginFragment()
    }

    override fun onLoginSuccess(username: String) {
        showHomeFragment(username)
    }

    override fun onSignUpClick() {
        showSignUpFragment()
    }

    override fun onRegistrationComplete(user: User) {
        showLoginFragment(user)
    }

    override fun onHaveAccountClick() {
        showLoginFragment()
    }


    /**
     * Обработка кнопки "Назад"
     */
    @SuppressLint("GestureBackNavigation", "MissingSuperCall")
    override fun onBackPressed() {
        // получаем текущий активный фрагмент
        val currentFragment = supportFragmentManager.findFragmentById(fragmentContainerId)

        when (currentFragment) {
            // если текущий фрагмент SignUpFragment - возвращаемся назад (к LoginFragment)
            is SignUpFragment -> {
                if (supportFragmentManager.backStackEntryCount > 0) {
                    // возвращаемся к предыдущему фрагменту в стеке
                    // (сейчас это может быть только LoginFragment)
                    supportFragmentManager.popBackStack()
                } else {
                    // если почему-то нет в стеке, показываем LoginFragment
                    showLoginFragment()
                }
            }

            // для всех остальных фрагментов - закрываем приложение
            else -> {
                // закрываем приложение, завершая Activity
                finishAffinity() // закрывает все Activity приложения
            }
        }
    }
}