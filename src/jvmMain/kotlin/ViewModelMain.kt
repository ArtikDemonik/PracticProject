import androidx.compose.runtime.MutableState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.awt.*
import kotlin.system.exitProcess

data class MyProcess(val directory: String, val name: String, val isActive: Boolean)
class ViewModelMain(){
    private val _processList = MutableStateFlow(listOf<MyProcess>())
    val processList = _processList.asStateFlow()

    fun addProcess(process: MyProcess){
        _processList.update {
            it + process
        }
    }
    fun hide(visible: MutableState<Boolean>){
        if (!SystemTray.isSupported()) {
            println("SystemTray is not supported")
            return
        }
        val image = Toolkit.getDefaultToolkit().getImage("C:\\Users\\ArtikDemonik\\Pictures\\photo_2023-07-08_21-55-29.jpg")

        val popup = PopupMenu()
        val trayIcon = TrayIcon(image, "Don't disturb me", popup)
        val tray = SystemTray.getSystemTray()
        val openItem = MenuItem("Open")
        val exitItem = MenuItem("Exit")
        exitItem.addActionListener { exitProcess(1) }
        openItem.addActionListener {
            visible.value = true
            tray.remove(trayIcon)
        }
        popup.add(openItem)
        popup.addSeparator()
        popup.add(exitItem)
        trayIcon.popupMenu = popup

        try {
            tray.add(trayIcon)
        } catch (e: AWTException) {
            println("TrayIcon could not be added.")
        }
    }
    fun checkActive(){
        CoroutineScope(Dispatchers.IO).launch{
            while (true){
                val list = getActiveProcessList()
                _processList.update {
                    it.map {process ->
                        process.copy(isActive = list.contains(process.copy(isActive = true)))
                    }
                }
            }
        }

    }
    private fun getActiveProcessList(): MutableList<MyProcess> {
        val activeProcess = mutableListOf<MyProcess>()
        ProcessHandle.allProcesses().forEach {
            if (!it.info().command().isEmpty) {
                val process = it.info().command().get()
                val regex = Regex("^(.*\\\\)(.*\\....)\$")
                val results = regex.findAll(process).iterator().next().groups
                val directory = results[1]!!
                val name = results[2]!!
                activeProcess += MyProcess(directory.value, name.value, true)
            }
        }
        return activeProcess
    }

    fun registerRoom(){

    }

}