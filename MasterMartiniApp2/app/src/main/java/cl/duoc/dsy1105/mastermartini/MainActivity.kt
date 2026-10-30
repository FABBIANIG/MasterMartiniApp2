package cl.duoc.dsy1105.mastermartini

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.dsy1105.mastermartini.ui.navigation.MasterMartiniAppScaffold
import cl.duoc.dsy1105.mastermartini.ui.theme.MasterMartiniAppTheme
import cl.duoc.dsy1105.mastermartini.viewmodel.MasterMartiniViewModel

/**
 * Actividad Principal (Punto de entrada de la aplicación).
 * Conecta el ciclo de vida de Android con Jetpack Compose (Material 3),
 * inicializa el ViewModel desacoplado (IE 2.2.1) y lanza el contenedor de navegación (IE 2.1.1).
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Aplicamos el tema visual de Master Martini (Material 3)
            MasterMartiniAppTheme {
                // Instanciamos el ViewModel para que sobreviva a giros de pantalla y cambios de vista
                val masterMartiniViewModel: MasterMartiniViewModel = viewModel()

                // Cargamos la estructura principal con TopAppBar, BottomBar y NavHost
                MasterMartiniAppScaffold(viewModel = masterMartiniViewModel)
            }
        }
    }
}