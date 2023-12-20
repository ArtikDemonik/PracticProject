import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.AwtWindow
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.awt.*
import kotlin.system.exitProcess


@Composable
@Preview
fun App(viewModel: ViewModelMain) {

}

@Composable
fun Registation(viewModel: ViewModelMain){
    
}

@Composable
fun MainMenu(viewModel: ViewModelMain){
    val processList = viewModel.processList.collectAsState()
    val fileDialogVisible = remember {
        mutableStateOf(false)
    }
    viewModel.checkActive()

    // TODO Отслеживать выбранное окно

    MaterialTheme {
        if (fileDialogVisible.value){
            FileDialog(){
                fileDialogVisible.value = false
                viewModel.addProcess(it!!)
            }
        }
        Column {

            Button(onClick = {
                fileDialogVisible.value = true
            }){
                Text("Add process")
            }

            LazyColumn {
                items(processList.value){
                    Card(modifier = Modifier.fillMaxWidth()){
                        var menuDropped by remember {
                            mutableStateOf(false)
                        }
                        val info = if(it.isActive) "is started" else "is not working now"
                        Row(modifier = Modifier.fillParentMaxSize()){
                            Text(it.name)
                            Text(info)
                            DropdownMenu(
                                expanded = menuDropped,
                                onDismissRequest = {
                                    menuDropped = false
                                }
                            ){
                                DropdownMenuItem(
                                    onClick = {

                                    }
                                ){
                                    Text("part of menu")
                                }
                            }
                        }
                    }
                }
            }
        }

    }
}




@Composable
private fun FileDialog(
    parent: Frame? = null,
    onCloseRequest: (result: MyProcess?) -> Unit
) = AwtWindow(
    create = {
        object : FileDialog(parent, "Choose a file", LOAD) {
            override fun setVisible(value: Boolean) {
                super.setVisible(value)
                if (value) {
                    onCloseRequest(MyProcess(directory, file, false))
                }
            }
        }
    },
    dispose = FileDialog::dispose
)




fun main() = application {
    val viewModel = ViewModelMain()
    val visible = remember{
        mutableStateOf(true)
    }
    Window(
        onCloseRequest = {
            visible.value= false
            CoroutineScope(Dispatchers.IO).launch {
                viewModel.hide(visible)
            }
                         },
        resizable = false,
        visible = visible.value
    ) {
        App(viewModel)
    }
}
