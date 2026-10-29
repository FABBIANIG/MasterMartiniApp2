package cl.duoc.dsy1105.mastermartini.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.TextIncrease
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import cl.duoc.dsy1105.mastermartini.ui.screens.CatalogoScreen
import cl.duoc.dsy1105.mastermartini.ui.screens.DetalleContenidoScreen
import cl.duoc.dsy1105.mastermartini.ui.screens.RegistroClienteScreen
import cl.duoc.dsy1105.mastermartini.viewmodel.MasterMartiniViewModel

/**
 * Componente de Navegación Principal con Material 3 (IE 2.1.1).
 * Incluye TopAppBar con botón de Accesibilidad Visual (Modo Texto Grande para >50 años)
 * y NavigationBar inferior clara y estructurada.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MasterMartiniAppScaffold(viewModel: MasterMartiniViewModel) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val rutaActual = backStackEntry?.destination?.route ?: "catalogo"
    val textoGrande by viewModel.modoTextoGrande.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Master Martini Chile",
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    // Botón de Accesibilidad en la barra superior (Punto 2.3 y 3.3 del caso)
                    FilledTonalButton(
                        onClick = { viewModel.alternarModoTextoGrande() },
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.TextIncrease,
                            contentDescription = "Cambiar tamaño de letra"
                        )
                        Text(
                            text = if (textoGrande) " Letra Normal" else " Letra Grande",
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = rutaActual == "catalogo" || rutaActual.startsWith("detalle"),
                    onClick = {
                        navController.navigate("catalogo") {
                            popUpTo("catalogo") { inclusive = true }
                        }
                    },
                    icon = { Icon(Icons.Default.MenuBook, contentDescription = "Catálogo") },
                    label = { Text("Recetas y Cursos") }
                )

                NavigationBarItem(
                    selected = rutaActual == "registro",
                    onClick = {
                        navController.navigate("registro") {
                            launchSingleTop = true
                        }
                    },
                    icon = { Icon(Icons.Default.PersonAdd, contentDescription = "Ficha Cliente") },
                    label = { Text("Mi Perfil / Registro") }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "catalogo",
            modifier = Modifier.padding(innerPadding)
        ) {
            // Vista 1: Catálogo de recetas, cursos y videos técnicos
            composable("catalogo") {
                CatalogoScreen(
                    viewModel = viewModel,
                    onIrADetalle = { contenidoId ->
                        navController.navigate("detalle/$contenidoId")
                    }
                )
            }

            // Vista 2: Detalle técnico, reproductor simulado y recursos nativos de compartir/llamada
            composable(
                route = "detalle/{contenidoId}",
                arguments = listOf(navArgument("contenidoId") { type = NavType.IntType })
            ) { backStack ->
                val id = backStack.arguments?.getInt("contenidoId") ?: 1
                DetalleContenidoScreen(
                    contenidoId = id,
                    viewModel = viewModel,
                    onVolver = { navController.popBackStack() }
                )
            }

            // Vista 3: Formulario validado con Cámara/Galería y base de datos SQLite
            composable("registro") {
                RegistroClienteScreen(viewModel = viewModel)
            }
        }
    }
}