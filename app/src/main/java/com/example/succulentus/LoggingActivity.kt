import android.util.Log
import androidx.appcompat.app.AppCompatActivity

/**
 * Базовый класс для всех активностей с логированием жизненного цикла
 *
 * Этот класс автоматически логирует все основные методы жизненного цикла Android
 * Просто наследуй от него вместо обычной AppCompatActivity
 */
open class LoggingActivity : AppCompatActivity() {

    // тег для фильтрации в Logcat - так мы легко найти сообщения
    companion object {
        private const val TAG = "LIFECYCLE_LOGGER"
    }

    // получаем имя текущей активности для понятного вывода
    private fun getCurrentActivityName(): String {
        return this::class.java.simpleName
    }

    // --------- МЕТОДЫ ЖИЗНЕННОГО ЦИКЛА ---------

    /**
     * Вызывается при ПЕРВОМ создании активности
     * Здесь обычно настраиваем интерфейс и загружаем данные
     */
    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, ">> ${getCurrentActivityName()} - СОЗДАНА (onCreate)")
    }

    /**
     * Вызывается когда активность становится ВИДИМОЙ пользователю
     */
    override fun onStart() {
        super.onStart()
        Log.d(TAG, ">> ${getCurrentActivityName()} - СТАРТУЕТ (onStart)")
    }

    /**
     * Вызывается когда активность СНОВА запускается после остановки
     * (не вызывается при первом запуске)
     */
    override fun onRestart() {
        super.onRestart()
        Log.d(TAG, ">> ${getCurrentActivityName()} - ПЕРЕЗАПУСК (onRestart)")
    }

    /**
     * Вызывается когда активность начинает ВЗАИМОДЕЙСТВОВАТЬ с пользователем
     */
    override fun onResume() {
        super.onResume()
        Log.d(TAG, ">> ${getCurrentActivityName()} - АКТИВНА (onResume)")
    }

    /**
     * Вызывается когда активность ТЕРЯЕТ фокус (например, появляется диалог)
     */
    override fun onPause() {
        super.onPause()
        Log.d(TAG, ">> ${getCurrentActivityName()} - ПАУЗА (onPause)")
    }

    /**
     * Вызывается когда активность перестает быть ВИДИМОЙ
     */
    override fun onStop() {
        super.onStop()
        Log.d(TAG, ">> ${getCurrentActivityName()} - ОСТАНОВЛЕНА (onStop)")
    }

    /**
     * Вызывается когда активность УНИЧТОЖАЕТСЯ
     */
    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, ">> ${getCurrentActivityName()} - УНИЧТОЖЕНА (onDestroy)")
    }
}