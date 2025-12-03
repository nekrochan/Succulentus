import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment

/**
 * Автоматически логирует все основные методы жизненного цикла фрагментов
 */
open class LoggingFragment : Fragment() {

    // тег для фильтрации в Logcat - так мы легко найдем сообщения
    companion object {
        private const val TAG = "LIFECYCLE_LOGGER"
    }

    // получаем имя текущего фрагмента для понятного вывода
    private fun getCurrentFragmentName(): String {
        return this::class.java.simpleName
    }

    // --------- МЕТОДЫ ЖИЗНЕННОГО ЦИКЛА ФРАГМЕНТА ---------

    /**
     * Вызывается при ПЕРВОМ создании фрагмента
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, ">> ${getCurrentFragmentName()} - СОЗДАН (onCreate)")
    }

    /**
     * Вызывается при создании View фрагмента
     */
    override fun onCreateView(
        inflater: android.view.LayoutInflater,
        container: android.view.ViewGroup?,
        savedInstanceState: Bundle?
    ): android.view.View? {
        Log.d(TAG, ">> ${getCurrentFragmentName()} - СОЗДАНИЕ VIEW (onCreateView)")
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    /**
     * Вызывается когда View фрагмента создана
     */
    override fun onViewCreated(view: android.view.View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, ">> ${getCurrentFragmentName()} - VIEW СОЗДАНА (onViewCreated)")
    }

    /**
     * Вызывается когда фрагмент становится ВИДИМЫМ
     */
    override fun onStart() {
        super.onStart()
        Log.d(TAG, ">> ${getCurrentFragmentName()} - СТАРТУЕТ (onStart)")
    }

    /**
     * Вызывается когда фрагмент начинает ВЗАИМОДЕЙСТВОВАТЬ с пользователем
     */
    override fun onResume() {
        super.onResume()
        Log.d(TAG, ">> ${getCurrentFragmentName()} - АКТИВЕН (onResume)")
    }

    /**
     * Вызывается когда фрагмент ТЕРЯЕТ фокус
     */
    override fun onPause() {
        super.onPause()
        Log.d(TAG, ">> ${getCurrentFragmentName()} - ПАУЗА (onPause)")
    }

    /**
     * Вызывается когда фрагмент перестает быть ВИДИМЫМ
     */
    override fun onStop() {
        super.onStop()
        Log.d(TAG, ">> ${getCurrentFragmentName()} - ОСТАНОВЛЕН (onStop)")
    }

    /**
     * Вызывается при уничтожении View фрагмента
     */
    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, ">> ${getCurrentFragmentName()} - VIEW УНИЧТОЖЕНА (onDestroyView)")
    }

    /**
     * Вызывается когда фрагмент УНИЧТОЖАЕТСЯ
     */
    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, ">> ${getCurrentFragmentName()} - УНИЧТОЖЕН (onDestroy)")
    }

    // --------- ДОПОЛНИТЕЛЬНЫЕ МЕТОДЫ ФРАГМЕНТА ---------

    /**
     * Вызывается при присоединении фрагмента к активности
     */
    override fun onAttach(context: android.content.Context) {
        super.onAttach(context)
        Log.d(TAG, ">> ${getCurrentFragmentName()} - ПРИСОЕДИНЕН к активности (onAttach)")
    }

    /**
     * Вызывается при создании активности фрагмента
     */
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.d(TAG, ">> ${getCurrentFragmentName()} - АКТИВНОСТЬ СОЗДАНА (onActivityCreated)")
    }

    /**
     * Вызывается при сохранении состояния фрагмента
     */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.d(TAG, ">> ${getCurrentFragmentName()} - СОХРАНЕНИЕ СОСТОЯНИЯ (onSaveInstanceState)")
    }

    /**
     * Вызывается при откреплении фрагмента от активности
     */
    override fun onDetach() {
        super.onDetach()
        Log.d(TAG, ">> ${getCurrentFragmentName()} - ОТКРЕПЛЕН от активности (onDetach)")
    }
}