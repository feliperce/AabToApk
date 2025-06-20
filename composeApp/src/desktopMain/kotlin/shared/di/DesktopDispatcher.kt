package shared.di

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.swing.Swing
import kotlinx.coroutines.test.setMain
import javax.swing.SwingUtilities

object DesktopDispatcher {
    fun initialize() {
        try {
            Dispatchers.setMain(Dispatchers.Swing)
        } catch (e: Exception) {
            println("Failed to set Main dispatcher: ${e.message}")
            e.printStackTrace()
        }
    }

    fun isMainThread(): Boolean {
        return SwingUtilities.isEventDispatchThread()
    }
}
